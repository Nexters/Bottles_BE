package com.nexters.bottles.auth.facade

import com.nexters.bottles.auth.component.AuthCodeGenerator
import com.nexters.bottles.auth.component.JwtTokenProvider
import com.nexters.bottles.auth.component.NaverSmsEncoder
import com.nexters.bottles.auth.facade.dto.*
import com.nexters.bottles.auth.service.AuthSmsService
import com.nexters.bottles.infra.WebClientAdapter
import com.nexters.bottles.user.service.UserService
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class AuthFacade(
    private val userService: UserService,
    private val authSmsService: AuthSmsService,
    private val webClientAdapter: WebClientAdapter,
    private val jwtTokenProvider: JwtTokenProvider,
    private val naverSmsEncoder: NaverSmsEncoder,
    private val authCodeGenerator: AuthCodeGenerator,
) {

    private val log = KotlinLogging.logger { }

    fun kakaoSignInUp(code: String): KakaoSignInUpResponse {
        val userInfoResponse = webClientAdapter.sendAuthRequest(code).convert()
        val user = userService.findUserOrSignUp(userInfoResponse)

        val accessToken = jwtTokenProvider.createAccessToken(user.id)
        val refreshToken = jwtTokenProvider.createRefreshToken(user.id)

        return KakaoSignInUpResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    fun requestSendSms(phoneNumber: String): SensSmsResponse {
        val currentTimeMillis = System.currentTimeMillis()
        val signature = naverSmsEncoder.generateSignature(currentTimeMillis)

        val authCode = authCodeGenerator.createRandomNumbers()
        val smsResponse = webClientAdapter.sendSms(
            time = currentTimeMillis,
            messageDto = MessageDTO(to = phoneNumber, content = authCode),
            signature = signature,
        )
        log.info { "requestId: ${smsResponse?.requestId}, statusCode: ${smsResponse?.statusCode}" }

        val authSms = authSmsService.saveAuthSms(
            phoneNumber = phoneNumber,
            authCode = authCode,
            expiredAt = LocalDateTime.now().plusMinutes(5)
        )

        return SensSmsResponse(expiredAt = authSms.expiredAt)
    }

    fun authSms(authSmsRequest: AuthSmsRequest) {
        val lastAuthSms = authSmsService.findLastAuthSms(authSmsRequest.phoneNumber)
        lastAuthSms.validate(lastAuthSms.authCode)
    }
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
