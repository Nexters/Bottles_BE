package com.nexters.bottles.auth.controller

import com.nexters.bottles.auth.facade.AuthFacade
import com.nexters.bottles.auth.facade.dto.KakaoSignInUpRequest
import com.nexters.bottles.auth.facade.dto.KakaoSignInUpResponse
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
}
