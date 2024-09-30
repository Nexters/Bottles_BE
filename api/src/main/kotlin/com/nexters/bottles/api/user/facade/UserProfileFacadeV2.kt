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
import com.nexters.bottles.app.user.service.BlockContactListService
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
class UserProfileFacadeV2(
    private val profileService: UserProfileService,
    private val fileService: FileService,
    private val imageUploader: ImageUploader,
) {

    private val log = KotlinLogging.logger { }

    fun upsertIntroductionAndImages(
        userId: Long,
        registerIntroductionRequest: RegisterIntroductionRequest,
        files: List<MultipartFile>
    ) {
        validateIntroduction(registerIntroductionRequest)

        profileService.saveIntroduction(userId, registerIntroductionRequest.introduction)

        val paths = files.map { makePathWithUserId(it, userId) }
        val originalImageUrls = files.zip(paths)
            .map { (file, path) -> fileService.upload(file, path) }
            .map { it.toString() }
        val blurredImageUrl = imageUploader.uploadWithBlur(files[0], paths[0]);

        profileService.uploadImageUrls(userId, originalImageUrls, blurredImageUrl.toString())
    }

    private fun validateIntroduction(introductionDto: RegisterIntroductionRequest) {
        introductionDto.introduction.forEach {
            require(it.answer.length >= 30 && it.answer.length <= 300) {
                "소개는 30자 이상 300자 이하로 써야 해요"
            }
        }
    }

    fun makePathWithUserId(
        file: MultipartFile,
        userId: Long
    ) = "" + userId + FILE_NAME_DELIMITER + LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + FILE_NAME_DELIMITER + file.originalFilename

    companion object {
        private const val FILE_NAME_DELIMITER = "_"
    }
}
