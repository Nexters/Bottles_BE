package com.nexters.bottles.api.auth.controller

import com.nexters.bottles.api.auth.facade.AuthFacade
import com.nexters.bottles.api.auth.facade.dto.SignUpResponse
import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.resolver.AuthUserId
import com.nexters.bottles.app.user.service.dto.SignUpProfileRequestV2
import com.nexters.bottles.app.user.service.dto.SignUpRequestV2
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2/auth")
class AuthControllerV2(
    private val authFacade: AuthFacade,
) {

    @ApiOperation("일반 회원가입하기 v2 - 문자")
    @PostMapping("/signup")
    fun signUp(@RequestBody signUpRequestV2: SignUpRequestV2): SignUpResponse {
        return authFacade.smsSignUpV2(signUpRequestV2)
    }

    @ApiOperation("일반 회원가입하기 v2 -  생년월일 / 성별 /이름 입력")
    @PostMapping("/profile")
    @AuthRequired
    fun signUpProfile(@AuthUserId userId: Long, @RequestBody signUpProfileRequestV2: SignUpProfileRequestV2) {
        return authFacade.registerSignupProfile(userId, signUpProfileRequestV2)
    }
}
