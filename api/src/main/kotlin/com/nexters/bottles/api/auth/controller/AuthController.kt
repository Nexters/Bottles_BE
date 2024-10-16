package com.nexters.bottles.api.auth.controller

import com.nexters.bottles.api.auth.facade.AuthFacade
import com.nexters.bottles.api.auth.facade.dto.AppleRevokeResponse
import com.nexters.bottles.api.auth.facade.dto.AppleSignInUpRequest
import com.nexters.bottles.api.auth.facade.dto.AppleSignInUpResponse
import com.nexters.bottles.api.auth.facade.dto.AuthSmsRequest
import com.nexters.bottles.api.auth.facade.dto.FcmUpdateRequest
import com.nexters.bottles.api.auth.facade.dto.KakaoSignInUpRequest
import com.nexters.bottles.api.auth.facade.dto.KakaoSignInUpResponse
import com.nexters.bottles.api.auth.facade.dto.LogoutRequest
import com.nexters.bottles.api.auth.facade.dto.ReissueTokenRequest
import com.nexters.bottles.api.auth.facade.dto.ReissueTokenResponse
import com.nexters.bottles.api.auth.facade.dto.SendSmsResponse
import com.nexters.bottles.api.auth.facade.dto.SignUpResponse
import com.nexters.bottles.api.auth.facade.dto.SmsSendRequest
import com.nexters.bottles.api.auth.facade.dto.SmsSignInRequest
import com.nexters.bottles.api.auth.facade.dto.SmsSignInResponse
import com.nexters.bottles.api.auth.facade.dto.UpdateAppVersionResponse
import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.interceptor.RefreshAuthRequired
import com.nexters.bottles.api.global.resolver.AccessToken
import com.nexters.bottles.api.global.resolver.AuthUserId
import com.nexters.bottles.api.global.resolver.RefreshTokenUserId
import com.nexters.bottles.app.user.service.dto.SignUpRequest
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
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
        return authFacade.kakaoSignInUp(kakaoSignInUpRequest)
    }

    @ApiOperation("애플 회원가입/로그인으로 엑세스 토큰 얻기")
    @PostMapping("/apple")
    fun appleSignInUp(@RequestBody appleSignInUpRequest: AppleSignInUpRequest): AppleSignInUpResponse {
        return authFacade.appleSignInUp(appleSignInUpRequest)
    }

    @ApiOperation("토큰 재발행")
    @PostMapping("/refresh")
    @RefreshAuthRequired
    fun reissueToken(
        @RefreshTokenUserId userId: Long,
        @RequestBody reissueTokenRequest: ReissueTokenRequest?
    ): ReissueTokenResponse {
        return authFacade.reissueToken(userId, reissueTokenRequest)
    }

    @ApiOperation("일반 회원가입")
    @PostMapping("/signup")
    fun signUp(@RequestBody signUpRequest: SignUpRequest): SignUpResponse {
        return authFacade.smsSignUp(signUpRequest)
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

    @ApiOperation("문자 로그인하기")
    @PostMapping("/sms/login")
    fun smsSignIn(@RequestBody smsSignInRequest: SmsSignInRequest): SmsSignInResponse {
        return authFacade.smsSignIn(smsSignInRequest)
    }

    @ApiOperation("로그아웃하기")
    @PostMapping("/logout")
    @AuthRequired
    fun logout(@AuthUserId userId: Long, @AccessToken accessToken: String, @RequestBody logoutRequest: LogoutRequest?) {
        authFacade.logout(userId, accessToken, logoutRequest)
    }

    @ApiOperation("회원 탈퇴하기")
    @PostMapping("/delete")
    @AuthRequired
    fun delete(@AuthUserId userId: Long, @AccessToken accessToken: String) {
        authFacade.delete(userId, accessToken)
    }

    @ApiOperation("애플 로그인 탈퇴를 위한 client secret 값 조회하기")
    @GetMapping("/apple/revoke")
    @AuthRequired
    fun getAppleClientSecret(): AppleRevokeResponse {
        return authFacade.getAppleClientSecret()
    }

    @ApiOperation("fcm 토큰 갱신")
    @PostMapping("/fcm")
    @AuthRequired
    fun insertFcmToken(@AuthUserId userId: Long, @RequestBody fcmUpdateRequest: FcmUpdateRequest) {
        authFacade.updateFcmToken(userId, fcmUpdateRequest.fcmToken)
    }

    @ApiOperation("업데이트 해야하는 앱 버전 조회")
    @GetMapping("/app-version")
    fun getUpdateAppVersion(): UpdateAppVersionResponse {
        return UpdateAppVersionResponse(
            minimumIosVersion = null,
            minimumAndroidVersion = 10009L,
            latestAndroidVersion = 10010L
        )
    }
}
