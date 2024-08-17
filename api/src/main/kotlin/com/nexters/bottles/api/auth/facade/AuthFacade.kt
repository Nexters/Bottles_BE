package com.nexters.bottles.api.auth.facade

import com.nexters.bottles.api.auth.component.AuthCodeGenerator
import com.nexters.bottles.api.auth.component.JwtTokenProvider
import com.nexters.bottles.api.auth.component.NaverSmsEncoder
import com.nexters.bottles.api.auth.component.event.DeleteUserEventDto
import com.nexters.bottles.api.auth.facade.dto.AuthSmsRequest
import com.nexters.bottles.api.auth.facade.dto.KakaoSignInUpResponse
import com.nexters.bottles.api.auth.facade.dto.MessageDto
import com.nexters.bottles.api.auth.facade.dto.RefreshAccessTokenResponse
import com.nexters.bottles.api.auth.facade.dto.SendSmsResponse
import com.nexters.bottles.api.auth.facade.dto.SignUpResponse
import com.nexters.bottles.api.auth.facade.dto.SmsSignInRequest
import com.nexters.bottles.api.auth.facade.dto.SmsSignInResponse
import com.nexters.bottles.api.infra.WebClientAdapter
import com.nexters.bottles.app.auth.service.AuthSmsService
import com.nexters.bottles.app.auth.service.BlackListService
import com.nexters.bottles.app.auth.service.RefreshTokenService
import com.nexters.bottles.app.user.service.UserProfileService
import com.nexters.bottles.app.user.service.UserService
import com.nexters.bottles.app.user.service.dto.KakaoUserInfoResponse
import com.nexters.bottles.app.user.service.dto.SignUpRequest
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class AuthFacade(
    private val userService: UserService,
    private val userProfileService: UserProfileService,
    private val authSmsService: AuthSmsService,
    private val blackListService: BlackListService,
    private val refreshTokenService: RefreshTokenService,
    private val webClientAdapter: WebClientAdapter,
    private val jwtTokenProvider: JwtTokenProvider,
    private val naverSmsEncoder: NaverSmsEncoder,
    private val authCodeGenerator: AuthCodeGenerator,
    private val applicationEventPublisher: ApplicationEventPublisher,

    @Value("\${super-user-number}")
    private val superUserNumber: String,
) {

    private val log = KotlinLogging.logger { }

    fun kakaoSignInUp(code: String): KakaoSignInUpResponse {
        val userInfoResponse = webClientAdapter.sendAuthRequest(code).convert()
        val signInUpDto = userService.findKakaoUserOrSignUp(userInfoResponse)
        val userProfile = userProfileService.findUserProfile(signInUpDto.userId)

        val accessToken = jwtTokenProvider.createAccessToken(signInUpDto.userId)
        val refreshToken = jwtTokenProvider.upsertRefreshToken(signInUpDto.userId)

        return KakaoSignInUpResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isSignUp = signInUpDto.isSignUp,
            hasCompleteUserProfile = userProfile != null,
            hasCompleteIntroduction = userProfile?.hasCompleteIntroduction() ?: false,
        )
    }

    fun refreshToken(userId: Long): RefreshAccessTokenResponse {
        val accessToken = jwtTokenProvider.createAccessToken(userId)
        val refreshToken = jwtTokenProvider.upsertRefreshToken(userId)

        return RefreshAccessTokenResponse(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun signUp(signUpRequest: SignUpRequest): SignUpResponse {
        val lastAuthSms = authSmsService.findLastAuthSms(signUpRequest.phoneNumber)
        lastAuthSms.validate(signUpRequest.authCode)

        val user = userService.signUp(signUpRequest)

        val accessToken = jwtTokenProvider.createAccessToken(user.id)
        val refreshToken = jwtTokenProvider.upsertRefreshToken(user.id)

        return SignUpResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun requestSendSms(phoneNumber: String): SendSmsResponse {
        val currentTimeMillis = System.currentTimeMillis()
        val signature = naverSmsEncoder.generateSignature(currentTimeMillis)

        val authCode = authCodeGenerator.createRandomNumbers()
        val smsResponse = webClientAdapter.sendSms(
            time = currentTimeMillis,
            messageDto = MessageDto(to = phoneNumber, content = authCode),
            signature = signature,
        )
        log.info { "requestId: ${smsResponse?.requestId}, statusCode: ${smsResponse?.statusCode}" }

        val expiredAt = LocalDateTime.now().plusMinutes(5)
        if (isSuperUser(phoneNumber)) {
            return SendSmsResponse(expiredAt = expiredAt)
        }
        val authSms = authSmsService.saveAuthSms(
            phoneNumber = phoneNumber,
            authCode = authCode,
            expiredAt = expiredAt
        )
        return SendSmsResponse(expiredAt = authSms.expiredAt)
    }

    fun authSms(authSmsRequest: AuthSmsRequest) {
        val lastAuthSms = authSmsService.findLastAuthSms(authSmsRequest.phoneNumber)
        lastAuthSms.validate(lastAuthSms.authCode)
    }

    fun logout(userId: Long, accessToken: String) {
        blackListService.add(accessToken)
        refreshTokenService.delete(userId)
    }

    fun delete(userId: Long) {
        userService.softDelete(userId)
        applicationEventPublisher.publishEvent(DeleteUserEventDto(userId = userId))
    }

    fun smsSignIn(smsSignInRequest: SmsSignInRequest): SmsSignInResponse {
        val lastAuthSms = authSmsService.findLastAuthSms(smsSignInRequest.phoneNumber)
        lastAuthSms.validate(smsSignInRequest.authCode)

        val user = userService.findByPhoneNumber(smsSignInRequest.phoneNumber)
            ?: throw IllegalArgumentException("회원가입에 대해 문의해주세요")

        val userProfile = userProfileService.findUserProfile(user.id)

        val accessToken = jwtTokenProvider.createAccessToken(user.id)
        val refreshToken = jwtTokenProvider.upsertRefreshToken(user.id)

        return SmsSignInResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            hasCompleteUserProfile = userProfile != null,
            hasCompleteIntroduction = userProfile?.hasCompleteIntroduction() ?: false,
        )
    }

    private fun isSuperUser(phoneNumber: String) = phoneNumber == superUserNumber
}

fun KakaoUserInfoResponse.convert(): KakaoUserInfoResponse {
    val updatedAccount =
        kakao_account.copy(
            phone_number = convertFromInternationalToKr(this.kakao_account.phone_number),
            birthDate = convertFromBirthdayToBirthDate(
                birthYear = this.kakao_account.birthyear,
                birthday = this.kakao_account.birthday
            )
        )

    return this.copy(kakao_account = updatedAccount)
}

/**
 * +82 10-1234-3456 으로 들어온 전화번호를  01012343456 으로 변환합니다.
 */
private fun convertFromInternationalToKr(phoneNumber: String): String {
    return phoneNumber
        .replace("+82 ", "0")
        .replace("-", "")
}

/**
 * 0424 으로 들어온 월일을 YearMonth로 바꾼다
 */
private fun convertFromBirthdayToBirthDate(birthYear: String, birthday: String): LocalDate {
    val year = birthYear.toInt()
    val month = birthday.substring(0, 2).toInt()
    val day = birthday.substring(2, 4).toInt()
    return LocalDate.of(year, month, day)
}
