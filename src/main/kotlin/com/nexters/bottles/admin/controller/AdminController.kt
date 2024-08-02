package com.nexters.bottles.admin.controller

import com.nexters.bottles.admin.facade.AdminFacade
import com.nexters.bottles.admin.facade.dto.CreateCustomTokenRequest
import com.nexters.bottles.admin.facade.dto.CustomTokenResponse
import com.nexters.bottles.global.interceptor.AuthRequired
import com.nexters.bottles.global.resolver.AuthUserId
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

    @ApiOperation("만료시간 입력받아서 토큰 발급받기")
    @PostMapping("/custom-token")
    @AuthRequired
    fun getCustomValidityToken(
        @AuthUserId userId: Long,
        @RequestBody createCustomTokenRequest: CreateCustomTokenRequest
    ): CustomTokenResponse {
        return adminFacade.createCustomValidityToken(userId, createCustomTokenRequest)
    }
}
