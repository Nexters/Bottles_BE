package com.nexters.bottles.api.user.controller

import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.resolver.AuthUserId
import com.nexters.bottles.api.user.facade.UserProfileFacade
import com.nexters.bottles.api.user.facade.UserProfileFacadeV2
import com.nexters.bottles.api.user.facade.dto.ActivateMatchingRequest
import com.nexters.bottles.api.user.facade.dto.ExistIntroductionResponse
import com.nexters.bottles.api.user.facade.dto.ProfileChoiceResponse
import com.nexters.bottles.api.user.facade.dto.RegisterIntroductionRequest
import com.nexters.bottles.api.user.facade.dto.RegisterProfileRequest
import com.nexters.bottles.api.user.facade.dto.UserInfoResponse
import com.nexters.bottles.api.user.facade.dto.UserProfileResponse
import com.nexters.bottles.api.user.facade.dto.UserProfileStatusResponse
import io.swagger.annotations.ApiOperation
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v2/profile")
class UserProfileControllerV2(
    private val profileFacadeV2: UserProfileFacadeV2,
) {

    @ApiOperation("마이페이지 자기소개 & 사진들 등록/수정하기")
    @PostMapping("/introduction-images")
    @AuthRequired
    fun upsertIntroductionAndImages(
        @AuthUserId userId: Long,
        @RequestBody registerIntroductionRequest: RegisterIntroductionRequest,
        @RequestPart files: List<MultipartFile>
    ) {
        profileFacadeV2.upsertIntroductionAndImages(userId, registerIntroductionRequest, files)
    }
}
