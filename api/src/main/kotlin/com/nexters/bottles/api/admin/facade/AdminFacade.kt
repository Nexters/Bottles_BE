package com.nexters.bottles.api.admin.facade

import com.nexters.bottles.api.admin.component.TestJwtTokenProvider
import com.nexters.bottles.api.admin.facade.dto.*
import com.nexters.bottles.api.auth.component.JwtTokenProvider
import com.nexters.bottles.api.user.facade.dto.InterestDto
import com.nexters.bottles.api.user.facade.dto.RegionDto
import com.nexters.bottles.app.admin.service.AdminService
import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.common.component.AmazonS3FileService
import com.nexters.bottles.app.common.component.ImageProcessor
import com.nexters.bottles.app.common.component.ImageUploader
import com.nexters.bottles.app.config.CacheType.Name.PING_PONG_BOTTLE_LIST
import com.nexters.bottles.app.notification.component.FcmClient
import com.nexters.bottles.app.notification.component.dto.FcmNotification
import com.nexters.bottles.app.notification.service.FcmTokenService
import com.nexters.bottles.app.user.domain.QuestionAndAnswer
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.domain.UserProfile
import com.nexters.bottles.app.user.domain.UserProfileSelect
import com.nexters.bottles.app.user.domain.enum.Gender
import com.nexters.bottles.app.user.domain.enum.SignUpType
import com.nexters.bottles.app.user.service.UserProfileService
import com.nexters.bottles.app.user.service.UserService
import mu.KotlinLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class AdminFacade(
    private val adminService: AdminService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val testJwtTokenProvider: TestJwtTokenProvider,
    private val fcmClient: FcmClient,
    private val fcmTokenService: FcmTokenService,
    private val userService: UserService,
    private val userProfileService: UserProfileService,
    private val imageProcessor: ImageProcessor,
    private val imageUploader: ImageUploader,
    private val amazonS3FileService: AmazonS3FileService,
) {

    private val log = KotlinLogging.logger { }

    fun createCustomValidityToken(
        userId: Long,
        createCustomTokenRequest: CreateCustomTokenRequest
    ): CustomTokenResponse {
        val accessTokenValidityInMilliseconds = createCustomTokenRequest.accessTime * 1000
        val refreshTokenValidityInMilliseconds = createCustomTokenRequest.refreshTime * 1000

        val accessToken = testJwtTokenProvider.createAccessToken(userId, accessTokenValidityInMilliseconds)
        val refreshToken = testJwtTokenProvider.upsertRefreshToken(userId, refreshTokenValidityInMilliseconds)

        return CustomTokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun forceAfterProfile(): ForceAfterProfileResponse {
        val accessToken1 = jwtTokenProvider.createAccessToken(mockMaleUser.id)
        val refreshToken1 = jwtTokenProvider.upsertRefreshToken(mockMaleUser.id)
        val accessToken2 = jwtTokenProvider.createAccessToken(mockFemaleUser1.id)
        val refreshToken2 = jwtTokenProvider.upsertRefreshToken(mockFemaleUser1.id)

        return ForceAfterProfileResponse(
            accessToken1 = accessToken1,
            refreshToken1 = refreshToken1,
            accessToken2 = accessToken2,
            refreshToken2 = refreshToken2,
        )
    }

    fun forceLogin(): ForceAfterProfileResponse {
        val accessToken1 = jwtTokenProvider.createAccessToken(mockMaleUser.id)
        val refreshToken1 = jwtTokenProvider.upsertRefreshToken(mockMaleUser.id)
        val accessToken2 = jwtTokenProvider.createAccessToken(mockFemaleUser1.id)
        val refreshToken2 = jwtTokenProvider.upsertRefreshToken(mockFemaleUser1.id)

        return ForceAfterProfileResponse(
            accessToken1 = accessToken1,
            refreshToken1 = refreshToken1,
            accessToken2 = accessToken2,
            refreshToken2 = refreshToken2,
        )
    }

    fun forceBottleReceive() {
        adminService.forceBottleReceive(
            mockMaleUser = mockMaleUser,
            mockFemaleUser = mockFemaleUser1,
            bottleStatus = BottleStatus.RANDOM,
            likeMessage = null,
        )
        adminService.forceBottleReceive(
            mockMaleUser = mockMaleUser,
            mockFemaleUser = mockFemaleUser2,
            bottleStatus = BottleStatus.SENT,
            likeMessage = "이상형이에요 🥰"
        )
    }

    private fun saveMockProfile(mockUserProfile: UserProfile) {
        adminService.saveMockProfile(mockUserProfile)
    }

    private fun saveMockUser(mockUser: User): User {
        adminService.saveMockUser(mockUser)
        return mockUser
    }

    fun expireToken(expireTokenRequest: ExpireTokenRequest, isAccessToken: Boolean) {
        if (isAccessToken) {
            adminService.expireAccessToken(expireTokenRequest.token)
        } else {
            val userId = jwtTokenProvider.getUserIdFromToken(expireTokenRequest.token, false)
                ?: throw IllegalArgumentException("유효하지 않은 리프레시 토큰입니다")
            adminService.expireRefreshToken(expireTokenRequest.token, userId)
        }
    }

    @Caching(
        evict = [
            CacheEvict(PING_PONG_BOTTLE_LIST, key = "1"),
            CacheEvict(PING_PONG_BOTTLE_LIST, key = "2"),
            CacheEvict(PING_PONG_BOTTLE_LIST, key = "9"),
            CacheEvict(PING_PONG_BOTTLE_LIST, key = "1 + '-v2'"),
            CacheEvict(PING_PONG_BOTTLE_LIST, key = "2 + '-v2'"),
            CacheEvict(PING_PONG_BOTTLE_LIST, key = "9 + '-v2'"),
        ]
    )
    fun forceCleanUp() {
        adminService.cleanUpMockUpData(mockMaleUser)
        adminService.cleanUpMockUpData(mockFemaleUser1)
        adminService.cleanUpMockUpData(mockFemaleUser2)
    }

    fun testFcm(fcmToken: String): String? {
        val fcmNotification = FcmNotification(
            title = "좋은 하루 되세요",
            body = "테스트입니다"
        )
        return fcmClient.sendNotificationTo(userToken = fcmToken, fcmNotification = fcmNotification)
    }

    fun sendPushMessages(pushMessagesRequest: PushMessageRequest) {
        val fcmNotification = FcmNotification(
            title = pushMessagesRequest.title,
            body = pushMessagesRequest.body
        )
        val userIds = pushMessagesRequest.userIds

        userIds.forEach {
            fcmTokenService.findAllByUserIdAndTokenNotBlank(it)
                .forEach { fcmToken ->
                    fcmClient.sendNotificationTo(userToken = fcmToken.token, fcmNotification = fcmNotification)
                    log.info { "수기 다건 발생으로 $it 에게 ${pushMessagesRequest.title} 푸시 발송" }
                }
        }
    }

    fun makeBlurImage(userId: Long) {
        userProfileService.findAllWithImage()
            .filter { it.user.id > userId }
            .filter { it.blurredImageUrl == null }
            .forEach {
                val imageFile = amazonS3FileService.downloadAsMultipartFile(it.imageUrl!!.substringAfterLast("/"))
                val path = makePathWithUserId(imageFile, it.user.id)
                val blurredImageUrl = imageUploader.uploadWithBlur(imageFile, path);

                userProfileService.addBlurImageUrl(it.id, blurredImageUrl.toString())
            }
    }

    fun makeMoreBlurImage(adminBlurImageRequest: AdminBlurImageRequest) {
        userProfileService.findAllWithImage()
            .filter { it.user.id >= adminBlurImageRequest.startUserId && it.user.id < adminBlurImageRequest.endUserId }
            .filter { it.imageUrl != null }
            .forEach {
                val imageFile = amazonS3FileService.downloadAsMultipartFile(it.imageUrl!!.substringAfterLast("/"))
                val path = makeFirstPathWithUserId(imageFile, it.user.id)
                val imageUrl = imageUploader.upload(imageFile, path).toString();
                val blurredImageUrl = imageUrl.replace(PREFIX_ORIGINAL_IMAGE_MAIN, PREFIX_BLURRED_IMAGE)

                userProfileService.upsertImageUrls(it.user.id, listOf(imageUrl), blurredImageUrl)
            }
    }

    fun makePathWithUserId(
        file: MultipartFile,
        userId: Long
    ) = "" + userId + FILE_NAME_DELIMITER + LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + FILE_NAME_DELIMITER + file.originalFilename

    private fun makeFirstPathWithUserId(
        file: MultipartFile,
        userId: Long
    ): String {
        val filePath = "$PREFIX_ORIGINAL_IMAGE_MAIN${userId}${FILE_NAME_DELIMITER}${
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        }${FILE_NAME_DELIMITER}${file.originalFilename}"

        return filePath
    }

    companion object {
        private const val FILE_NAME_DELIMITER = "_"
        private const val PREFIX_ORIGINAL_IMAGE_MAIN = "original/main/"
        private const val PREFIX_ORIGINAL_IMAGE = "original/"
        private const val PREFIX_BLURRED_IMAGE = "blur/"
        private const val mockMaleUserId = 1L
        private const val mockFemaleUserId1 = 2L
        private const val mockFemaleUserId2 = 9L

        val mockMaleUser = User(
            id = mockMaleUserId,
            birthdate = LocalDate.of(1999, 8, 1),
            name = "차은우",
            gender = Gender.MALE,
            kakaoId = "chaenu123",
            phoneNumber = "01012345678",
            signUpType = SignUpType.KAKAO,
        )

        val mockFemaleUser1 = User(
            id = mockFemaleUserId1,
            birthdate = LocalDate.of(1999, 3, 1),
            name = "carina",
            gender = Gender.FEMALE,
            kakaoId = "carina123",
            phoneNumber = "01011112222",
            signUpType = SignUpType.KAKAO,
        )

        val mockFemaleUser2 = User(
            id = mockFemaleUserId2,
            birthdate = LocalDate.of(1999, 3, 1),
            name = "카리나",
            gender = Gender.FEMALE,
            kakaoId = "carina123",
            phoneNumber = "01011112222",
            signUpType = SignUpType.KAKAO,
        )

        val mockMaleUserProfile = UserProfile(
            user = mockMaleUser,
            profileSelect = UserProfileSelect(
                mbti = "intj",
                keyword = listOf("다정한", "적극적인", "신중한"),
                interest = InterestDto(
                    culture = listOf("전시회 방문", "공연 관람"),
                    sports = listOf("헬스", "러닝"),
                    entertainment = listOf("독서"),
                    etc = listOf()
                ).toDomain(),
                job = "직장인",
                height = 175,
                smoking = "가끔 피워요",
                alcohol = "때에 따라 적당히 즐겨요",
                religion = "무교",
                region = RegionDto("서울특별시", "강남구").toDomain()
            ),
            introduction = listOf(
                QuestionAndAnswer(
                    "보틀에 담을 소개를 작성해 주세요", """호기심 많고 새로운 경험을 즐깁니다.
                |주말엔 책을 읽거나 맛집을 찾아 다니며 여유를 즐기고, 친구들과 소소한 모임으로 충전해요. 일상에서 소소한 행복을 찾아요.""".trimMargin()
                )
            ),
            imageUrl = "https://bottles-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5%E1%86%BC.jpeg_20240730233759_1"
        )

        val mockFemaleUserProfile1 = UserProfile(
            user = mockFemaleUser1,
            profileSelect = UserProfileSelect(
                mbti = "intj",
                keyword = listOf("다정한", "적극적인", "신중한"),
                interest = InterestDto(
                    culture = listOf("전시회 방문", "공연 관람"),
                    sports = listOf("헬스", "러닝"),
                    entertainment = listOf("독서"),
                    etc = listOf()
                ).toDomain(),
                job = "직장인",
                height = 163,
                smoking = "가끔 피워요",
                alcohol = "때에 따라 적당히 즐겨요",
                religion = "무교",
                region = RegionDto("서울특별시", "강남구").toDomain()
            ),
            introduction = listOf(
                QuestionAndAnswer(
                    "보틀에 담을 소개를 작성해 주세요", """호기심 많고 새로운 경험을 즐깁니다.
                |주말엔 책을 읽거나 맛집을 찾아 다니며 여유를 즐기고, 친구들과 소소한 모임으로 충전해요. 일상에서 소소한 행복을 찾아요.""".trimMargin()
                )
            ),
            imageUrl = "https://bottles-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5%E1%86%BC.jpeg_20240730233759_1"
        )
        val mockFemaleUserProfile2 = UserProfile(
            user = mockFemaleUser2,
            profileSelect = UserProfileSelect(
                mbti = "intj",
                keyword = listOf("다정한", "적극적인", "신중한"),
                interest = InterestDto(
                    culture = listOf("전시회 방문", "공연 관람"),
                    sports = listOf("헬스", "러닝"),
                    entertainment = listOf("독서"),
                    etc = listOf()
                ).toDomain(),
                job = "직장인",
                height = 163,
                smoking = "가끔 피워요",
                alcohol = "때에 따라 적당히 즐겨요",
                religion = "무교",
                region = RegionDto("서울특별시", "강남구").toDomain()
            ),
            introduction = listOf(
                QuestionAndAnswer(
                    "보틀에 담을 소개를 작성해 주세요", """호기심 많고 새로운 경험을 즐깁니다.
                |주말엔 책을 읽거나 맛집을 찾아 다니며 여유를 즐기고, 친구들과 소소한 모임으로 충전해요. 일상에서 소소한 행복을 찾아요.""".trimMargin()
                )
            ),
            imageUrl = "https://bottles-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5%E1%86%BC.jpeg_20240730233759_1"
        )
    }
}
