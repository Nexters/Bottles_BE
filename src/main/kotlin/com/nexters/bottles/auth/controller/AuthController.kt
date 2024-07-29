package com.nexters.bottles.auth.controller

import com.nexters.bottles.auth.facade.AuthFacade
import com.nexters.bottles.auth.facade.dto.AuthSmsRequest
import com.nexters.bottles.auth.facade.dto.KakaoSignInUpRequest
import com.nexters.bottles.auth.facade.dto.KakaoSignInUpResponse
import com.nexters.bottles.auth.facade.dto.SendSmsResponse
import com.nexters.bottles.auth.facade.dto.SignUpRequest
import com.nexters.bottles.auth.facade.dto.SignUpResponse
import com.nexters.bottles.auth.facade.dto.SmsSendRequest
import com.nexters.bottles.auth.facade.dto.*
import com.nexters.bottles.global.resolver.RefreshTokenUserId
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authFacade: AuthFacade,
) {

    @ApiOperation("카카오 회원가입/로그인으로 엑세스 토큰 얻기")
    @PostMapping("/kakao")
    fun kakaoSignInUp(@RequestBody kakaoSignInUpRequest: KakaoSignInUpRequest): KakaoSignInUpResponse {
        return authFacade.kakaoSignInUp(kakaoSignInUpRequest.code)
    }

    @ApiOperation("토큰 재발행")
    @PostMapping("/refresh")
    fun requestRefresh(@RefreshTokenUserId userId: Long): RefreshAccessTokenResponse {
        return authFacade.refreshToken(userId)
    }

    @ApiOperation("일반 회원가입")
    @PostMapping("/signup")
    fun signUp(@RequestBody signUpRequest: SignUpRequest): SignUpResponse {
        return authFacade.signUp(signUpRequest)
    }

    @ApiOperation("문자 인증 발송 요청하기")
    @PostMapping("/sms/send")
    fun requestSmsSend(@RequestBody authSmsRequest: SmsSendRequest): SendSmsResponse {
        return authFacade.requestSendSms(authSmsRequest.phoneNumber)
    }

    @ApiOperation("문자 인증하기")
    @PostMapping("/sms/send/check")
    fun authSms(@RequestBody authSmsRequest: AuthSmsRequest) {
        authFacade.authSms(authSmsRequest)
    }
}
