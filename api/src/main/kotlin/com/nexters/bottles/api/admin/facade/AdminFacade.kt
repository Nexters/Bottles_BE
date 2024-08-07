package com.nexters.bottles.api.admin.facade

import com.nexters.bottles.api.admin.facade.dto.CreateCustomTokenRequest
import com.nexters.bottles.api.admin.facade.dto.CustomTokenResponse
import com.nexters.bottles.api.admin.facade.dto.ExpireTokenRequest
import com.nexters.bottles.api.admin.facade.dto.ForceAfterProfileResponse
import com.nexters.bottles.api.user.facade.dto.InterestDto
import com.nexters.bottles.api.user.facade.dto.RegionDto
import com.nexters.bottles.app.admin.component.TestJwtTokenProvider
import com.nexters.bottles.app.admin.service.AdminService
import com.nexters.bottles.app.auth.service.JwtTokenProvider
import com.nexters.bottles.app.user.domain.QuestionAndAnswer
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.domain.UserProfile
import com.nexters.bottles.app.user.domain.UserProfileSelect
import com.nexters.bottles.app.user.domain.enum.Gender
import com.nexters.bottles.app.user.domain.enum.SignUpType
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AdminFacade(
    private val adminService: AdminService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val testJwtTokenProvider: TestJwtTokenProvider,
) {

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
        adminService.cleanUpMockUpData(mockMaleUser)
        adminService.cleanUpMockUpData(mockFemaleUser)

        val accessToken1 = jwtTokenProvider.createAccessToken(mockMaleUser.id)
        val refreshToken1 = jwtTokenProvider.upsertRefreshToken(mockMaleUser.id)
        val accessToken2 = jwtTokenProvider.createAccessToken(mockFemaleUser.id)
        val refreshToken2 = jwtTokenProvider.upsertRefreshToken(mockFemaleUser.id)

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
        val accessToken2 = jwtTokenProvider.createAccessToken(mockFemaleUser.id)
        val refreshToken2 = jwtTokenProvider.upsertRefreshToken(mockFemaleUser.id)

        return ForceAfterProfileResponse(
            accessToken1 = accessToken1,
            refreshToken1 = refreshToken1,
            accessToken2 = accessToken2,
            refreshToken2 = refreshToken2,
        )
    }

    fun forceBottleReceive() {
        adminService.forceBottleReceive(
            mockMaleUser,
            mockFemaleUser
        )
    }

    private fun saveMockProfile(mockUserProfile: UserProfile) {
        adminService.saveMockProfile(mockUserProfile)
    }

    private fun saveMockUser(mockUser: User): User {
        adminService.saveMockUser(mockUser)
        return mockUser
    }

    companion object {
        private const val mockMaleUserId = 1L
        private const val mockFemaleUserId = 2L

        val mockMaleUser = User(
            id = mockMaleUserId,
            birthdate = LocalDate.of(1999, 8, 1),
            name = "차은우",
            gender = Gender.MALE,
            kakaoId = "chaenu123",
            phoneNumber = "01012345678",
            signUpType = SignUpType.KAKAO,
        )

        val mockFemaleUser = User(
            id = mockFemaleUserId,
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
            blurredImageUrl = "https://bottles-bucket.s3.ap-northeast-2.amazonaws.com/blurred_%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5%E1%86%BC.jpeg_20240730233759_1",
            imageUrl = "https://bottles-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5%E1%86%BC.jpeg_20240730233759_1"
        )

        val mockFemaleUserProfile = UserProfile(
            user = mockFemaleUser,
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
            blurredImageUrl = "https://bottles-bucket.s3.ap-northeast-2.amazonaws.com/blurred_%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5%E1%86%BC.jpeg_20240730233759_1",
            imageUrl = "https://bottles-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5%E1%86%BC.jpeg_20240730233759_1"
        )
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
}
