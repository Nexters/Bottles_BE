package com.nexters.bottles.api.user.facade

import com.nexters.bottles.api.user.facade.dto.ActivateMatchingRequest
import com.nexters.bottles.api.user.facade.dto.ExistIntroductionResponse
import com.nexters.bottles.api.user.facade.dto.ProfileChoiceResponse
import com.nexters.bottles.api.user.facade.dto.RegisterIntroductionRequest
import com.nexters.bottles.api.user.facade.dto.RegisterProfileRequest
import com.nexters.bottles.api.user.facade.dto.UserInfoResponse
import com.nexters.bottles.api.user.facade.dto.UserProfileResponse
import com.nexters.bottles.api.user.facade.dto.UserProfileStatus
import com.nexters.bottles.api.user.facade.dto.UserProfileStatusResponse
import com.nexters.bottles.app.common.component.FileService
import com.nexters.bottles.app.common.component.ImageUploader
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.domain.UserProfile
import com.nexters.bottles.app.user.domain.UserProfileSelect
import com.nexters.bottles.app.user.service.UserProfileService
import com.nexters.bottles.app.user.service.UserService
import com.nexters.bottles.app.user.service.dto.SignInUpStep
import com.nexters.bottles.app.user.service.dto.SignInUpStep.SIGN_UP_APPLE_LOGIN_FINISHED
import com.nexters.bottles.app.user.service.dto.SignInUpStep.SIGN_UP_NAME_GENDER_AGE_FINISHED
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import regions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class UserProfileFacade(
    private val profileService: UserProfileService,
    private val userService: UserService,
    private val fileService: FileService,
    private val imageUploader: ImageUploader,
) {

    private val log = KotlinLogging.logger { }

    fun upsertProfile(userId: Long, profileDto: RegisterProfileRequest) {
        validateProfile(profileDto)
        val convertedProfileDto = convertProfileDto(profileDto)

        profileService.upsertProfile(
            userId = userId,
            profileSelect = UserProfileSelect(
                mbti = convertedProfileDto.mbti,
                keyword = convertedProfileDto.keyword,
                interest = convertedProfileDto.interest.toDomain(),
                job = convertedProfileDto.job,
                height = convertedProfileDto.height,
                smoking = convertedProfileDto.smoking,
                alcohol = convertedProfileDto.alcohol,
                religion = convertedProfileDto.religion,
                region = convertedProfileDto.region.toDomain(),
            )
        )
        userService.addKakaoIdAndRegion(
            userId = userId,
            kakaoId = profileDto.kakaoId,
            city = convertedProfileDto.region.city,
            state = convertedProfileDto.region.state,
        )
    }

    fun getProfileChoice(): ProfileChoiceResponse {
        return ProfileChoiceResponse(
            regions = regions
        )
    }

    fun upsertIntroduction(userId: Long, registerIntroductionRequest: RegisterIntroductionRequest) {
        validateIntroduction(registerIntroductionRequest)

        profileService.saveIntroduction(userId, registerIntroductionRequest.introduction)
    }

    fun getMyProfile(userId: Long): UserProfileResponse {
        val userProfile = profileService.findUserProfile(userId)
        val user = userProfile?.user ?: userService.findByIdAndNotDeleted(userId)
        return UserProfileResponse(
            userName = user.name,
            age = user.getKoreanAge(),
            kakaoId = user.kakaoId,
            imageUrl = userProfile?.imageUrl,
            introduction = userProfile?.introduction ?: emptyList(),
            profileSelect = userProfile?.profileSelect,
            isMatchActivated = user.isMatchActivated
        )
    }

    private fun validateProfile(profileDto: RegisterProfileRequest) {
        require(profileDto.keyword.size in 3..5) {
            "키워드는 최소 3개, 최대 5개까지 선택할 수 있어요"
        }
        val interestCount = profileDto.interest.culture.size + profileDto.interest.sports.size +
                profileDto.interest.entertainment.size + profileDto.interest.etc.size
        require(interestCount in 3..10) {
            "취미는 최소 3개, 최대 10개까지 선택할 수 있어요"
        }
    }

    private fun validateIntroduction(introductionDto: RegisterIntroductionRequest) {
        introductionDto.introduction.forEach {
            require(it.answer.length >= 30 && it.answer.length <= 300) {
                "소개는 30자 이상 300자 이하로 써야 해요"
            }
        }
    }

    private fun convertProfileDto(profileDto: RegisterProfileRequest): RegisterProfileRequest {
        when (profileDto.smoking) {
            "전혀 피우지 않아요" -> profileDto.smoking = "흡연 안해요"
            "가끔 피워요" -> profileDto.smoking = "흡연은 가끔"
            "자주 피워요" -> profileDto.smoking = "흡연해요"
        }
        when (profileDto.alcohol) {
            "한 방울도 마시지 않아요" -> profileDto.alcohol = "술은 안해요"
            "때에 따라 적당히 즐겨요" -> profileDto.alcohol = "술은 적당히"
            "자주 찾는 편이에요" -> profileDto.alcohol = "술을 즐겨요"
        }
        return profileDto
    }

    fun uploadImage(userId: Long, file: MultipartFile) {
        val me = userService.findByIdAndNotDeleted(userId)
        val path = makePathWithUserId(file, me.id)
        val originalImageUrl = fileService.upload(file, path)
        val blurredImageUrl = imageUploader.uploadWithBlur(file, path);

        profileService.uploadImageUrl(me, originalImageUrl.toString(), blurredImageUrl.toString())
    }

    private fun makePathWithUserId(
        file: MultipartFile,
        userId: Long
    ) = "" + userId + FILE_NAME_DELIMITER + LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + FILE_NAME_DELIMITER + file.originalFilename

    fun existIntroduction(userId: Long): ExistIntroductionResponse {
        val userProfile = profileService.findUserProfile(userId)
        return ExistIntroductionResponse(isExist = userProfile?.introduction?.isNotEmpty() ?: false)
    }

    fun findMyInfo(userId: Long): UserInfoResponse {
        val user = userService.findByIdAndNotDeleted(userId)
        return UserInfoResponse(name = user.name, signInUpStep = getSignInUpStep(user))
    }

    /**
     * 애플 로그인으로 가입하는 경우 성별/나이를 입력 받지 못해 서버에서 '오늘' 날짜를 입력해줍니다.
     * 성별/나이를 입력받지 않아도 화면상으로 다 입력 받은 다음 단계로 가야하기 때문에 2024년 출생으로
     * 되어있으면 이름/성별/나이를 입력했다고 내려줍니다.
     */
    private fun getSignInUpStep(user: User): SignInUpStep {
        return if (user.birthdate?.year == 2024) {
            SIGN_UP_NAME_GENDER_AGE_FINISHED
        } else if (user.name == null || user.birthdate == null || user.gender == null) {
            SIGN_UP_APPLE_LOGIN_FINISHED
        } else {
            SIGN_UP_NAME_GENDER_AGE_FINISHED
        }
    }

    fun findUserProfileStatus(userId: Long): UserProfileStatusResponse {
        val user = userService.findByIdAndNotDeleted(userId)
        val userProfile = profileService.findUserProfile(user.id)
        return UserProfileStatusResponse(
            getUserProfileStatus(userProfile)
        )
    }

    private fun getUserProfileStatus(userProfile: UserProfile?): UserProfileStatus {
        return when {
            userProfile?.imageUrl != null -> UserProfileStatus.PHOTO_DONE
            !userProfile?.introduction.isNullOrEmpty() -> UserProfileStatus.INTRODUCE_DONE
            userProfile != null -> UserProfileStatus.ONLY_PROFILE_CREATED
            else -> UserProfileStatus.EMPTY
        }
    }

    fun activateMatching(userId: Long, activateMatchingRequest: ActivateMatchingRequest) {
        userService.activateMatching(userId, activateMatchingRequest.activate)
    }

    companion object {
        private const val FILE_NAME_DELIMITER = "_"
    }
}
