package com.nexters.bottles.api.admin.controller

import com.nexters.bottles.api.admin.facade.AdminFacade
import com.nexters.bottles.api.admin.facade.dto.CreateCustomTokenRequest
import com.nexters.bottles.api.admin.facade.dto.CustomTokenResponse
import com.nexters.bottles.api.admin.facade.dto.ExpireTokenRequest
import com.nexters.bottles.api.admin.facade.dto.ForceAfterProfileResponse
import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.resolver.AuthUserId
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val adminFacade: AdminFacade,
) {

    @ApiOperation("카카오 회원가입하고 프로필 작성한 상태 만들기")
    @PostMapping("/after-profile")
    fun forceAfterProfile(): ForceAfterProfileResponse {
        return adminFacade.forceAfterProfile()
    }

    @ApiOperation("남녀 테스트 계정 매칭 초기화하기")
    @PostMapping("/cleanup")
    fun forceCleanUp() {
        return adminFacade.forceCleanUp()
    }

    @ApiOperation("로그인 하기")
    @PostMapping("/login")
    fun login(): ForceAfterProfileResponse {
        return adminFacade.forceAfterProfile()
    }

    @ApiOperation("차은우에게 보틀 도착한 상태 만들기 - 떠다니는 것 1개, 마음이 담긴것 1개")
    @PostMapping("/after-bottle-receive")
    fun forceBottleReceive() {
        adminFacade.forceBottleReceive()
    }

    @ApiOperation("만료시간 입력받아서 토큰 발급받기")
    @PostMapping("/custom-token")
    @AuthRequired
    fun getCustomValidityToken(
        @AuthUserId userId: Long,
        @RequestBody createCustomTokenRequest: CreateCustomTokenRequest
    ): CustomTokenResponse {
        return adminFacade.createCustomValidityToken(userId, createCustomTokenRequest)
    }

    @ApiOperation("액세스 토큰 만료시키기")
    @PostMapping("/expire/access-token")
    fun expireAccessToken(
        @RequestBody expireTokenRequest: ExpireTokenRequest
    ) {
        return adminFacade.expireToken(expireTokenRequest, true)
    }

    @ApiOperation("리프레시 토큰 만료시키기")
    @PostMapping("/expire/refresh-token")
    fun expireRefreshToken(
        @RequestBody expireTokenRequest: ExpireTokenRequest
    ) {
        return adminFacade.expireToken(expireTokenRequest, false)
    }
}
