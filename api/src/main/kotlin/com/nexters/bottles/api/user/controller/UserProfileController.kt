package com.nexters.bottles.api.user.controller

import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.resolver.AuthUserId
import com.nexters.bottles.api.user.facade.UserProfileFacade
import com.nexters.bottles.api.user.facade.dto.ActivateMatchingRequest
import com.nexters.bottles.api.user.facade.dto.ExistIntroductionResponse
import com.nexters.bottles.api.user.facade.dto.ProfileChoiceResponse
import com.nexters.bottles.api.user.facade.dto.RegisterIntroductionRequest
import com.nexters.bottles.api.user.facade.dto.RegisterProfileRequest
import com.nexters.bottles.api.user.facade.dto.UserInfoResponse
import com.nexters.bottles.api.user.facade.dto.UserProfileResponse
import com.nexters.bottles.api.user.facade.dto.UserProfileStatusResponse
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/profile")
class UserProfileController(
    private val profileFacade: UserProfileFacade,
) {

    @ApiOperation("온보딩 프로필 등록하기")
    @PostMapping("/choice")
    @AuthRequired
    fun upsertProfile(@AuthUserId userId: Long, @RequestBody registerProfileRequest: RegisterProfileRequest) {
        profileFacade.upsertProfile(userId, registerProfileRequest)
    }

    @ApiOperation("온보딩 선택지 조회하기 - 지역")
    @GetMapping("/choice")
    fun getProfileChoiceList(): ProfileChoiceResponse {
        return profileFacade.getProfileChoice()
    }

    @ApiOperation("마이페이지 자기소개 등록하기")
    @PostMapping("/introduction")
    @AuthRequired
    fun upsertIntroduction(
        @AuthUserId userId: Long,
        @RequestBody registerIntroductionRequest: RegisterIntroductionRequest
    ) {
        profileFacade.upsertIntroduction(userId, registerIntroductionRequest)
    }

    @ApiOperation("마이페이지 내 프로필 조회하기")
    @GetMapping
    @AuthRequired
    fun getProfile(@AuthUserId userId: Long): UserProfileResponse {
        return profileFacade.getMyProfile(userId)
    }

    @ApiOperation("마이페이지 사진 등록하기")
    @PostMapping("/images")
    @AuthRequired
    fun uploadImage(@AuthUserId userId: Long, @RequestPart file: MultipartFile) {
        profileFacade.uploadImage(userId, file)
    }

    @ApiOperation("자기소개 작성 여부 조회하기")
    @GetMapping("/introduction/exist")
    @AuthRequired
    fun existIntroduction(@AuthUserId userId: Long): ExistIntroductionResponse {
        return profileFacade.existIntroduction(userId)
    }

    @ApiOperation("유저 이름 및 가입 상태 조회")
    @GetMapping("/info")
    @AuthRequired
    fun findInfo(@AuthUserId userId: Long): UserInfoResponse {
        return profileFacade.findMyInfo(userId)
    }

    @ApiOperation("유저 프로필 상태 조회")
    @GetMapping("/status")
    @AuthRequired
    fun findStatus(@AuthUserId userId: Long): UserProfileStatusResponse {
        return profileFacade.findUserProfileStatus(userId)
    }

    @ApiOperation("매칭 활성/비활성화")
    @PostMapping("/activate/matching")
    @AuthRequired
    fun activateAccount(@AuthUserId userId: Long, @RequestBody activateMatchingRequest: ActivateMatchingRequest) {
        profileFacade.activateMatching(userId, activateMatchingRequest)
    }
}
