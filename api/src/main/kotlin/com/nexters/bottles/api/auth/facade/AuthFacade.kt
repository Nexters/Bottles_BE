package com.nexters.bottles.api.auth.facade

import com.nexters.bottles.api.auth.component.AppleAuthClientSecretKeyGenerator
import com.nexters.bottles.api.auth.component.ApplePublicKeyGenerator
import com.nexters.bottles.api.auth.component.AuthCodeGenerator
import com.nexters.bottles.api.auth.component.JwtTokenProvider
import com.nexters.bottles.api.auth.component.NaverSmsEncoder
import com.nexters.bottles.api.auth.component.event.DeleteUserEventDto
import com.nexters.bottles.api.auth.facade.dto.AppleRevokeResponse
import com.nexters.bottles.api.auth.facade.dto.AppleSignInUpRequest
import com.nexters.bottles.api.auth.facade.dto.AppleSignInUpResponse
import com.nexters.bottles.api.auth.facade.dto.AuthSmsRequest
import com.nexters.bottles.api.auth.facade.dto.KakaoSignInUpRequest
import com.nexters.bottles.api.auth.facade.dto.KakaoSignInUpResponse
import com.nexters.bottles.api.auth.facade.dto.LogoutRequest
import com.nexters.bottles.api.auth.facade.dto.MessageDto
import com.nexters.bottles.api.auth.facade.dto.ReissueTokenRequest
import com.nexters.bottles.api.auth.facade.dto.ReissueTokenResponse
import com.nexters.bottles.api.auth.facade.dto.SendSmsResponse
import com.nexters.bottles.api.auth.facade.dto.SignUpResponse
import com.nexters.bottles.api.auth.facade.dto.SignUpResponseV2
import com.nexters.bottles.api.auth.facade.dto.SmsSignInRequest
import com.nexters.bottles.api.auth.facade.dto.SmsSignInResponse
import com.nexters.bottles.api.infra.WebClientAdapter
import com.nexters.bottles.app.auth.service.AuthSmsService
import com.nexters.bottles.app.auth.service.BlackListService
import com.nexters.bottles.app.auth.service.RefreshTokenService
import com.nexters.bottles.app.notification.service.FcmTokenService
import com.nexters.bottles.app.user.service.UserProfileService
import com.nexters.bottles.app.user.service.UserService
import com.nexters.bottles.app.user.service.dto.KakaoUserInfoResponse
import com.nexters.bottles.app.user.service.dto.SignUpProfileRequestV2
import com.nexters.bottles.app.user.service.dto.SignUpRequest
import com.nexters.bottles.app.user.service.dto.SignUpRequestV2
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
    private val fcmTokenService: FcmTokenService,
    private val blackListService: BlackListService,
    private val refreshTokenService: RefreshTokenService,
    private val webClientAdapter: WebClientAdapter,
    private val jwtTokenProvider: JwtTokenProvider,
    private val naverSmsEncoder: NaverSmsEncoder,
    private val authCodeGenerator: AuthCodeGenerator,
    private val applePublicKeyGenerator: ApplePublicKeyGenerator,
    private val appleAuthClientSecretKeyGenerator: AppleAuthClientSecretKeyGenerator,
    private val applicationEventPublisher: ApplicationEventPublisher,

    @Value("\${super-user-number}")
    private val superUserNumber: String,
    @Value("\${super-user-number-v2}")
    private val superUserNumberV2: String,
) {

    private val log = KotlinLogging.logger { }

    fun kakaoSignInUp(kakaoSignInUpRequest: KakaoSignInUpRequest): KakaoSignInUpResponse {
        val userInfoResponse = webClientAdapter.sendKakaoAuthRequest(kakaoSignInUpRequest.code).convert()
        val signInUpDto = userService.findKakaoUserOrSignUp(userInfoResponse)
        val userProfile = userProfileService.findUserProfile(signInUpDto.userId)
        kakaoSignInUpRequest.fcmDeviceToken?.let {
            fcmTokenService.registerFcmToken(signInUpDto.userId, kakaoSignInUpRequest.fcmDeviceToken)
        }

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

    fun appleSignInUp(appleSignInUpRequest: AppleSignInUpRequest): AppleSignInUpResponse {
        val applePublicKeys = webClientAdapter.sendAppleAuthKeysRequest()
        val tokenHeaders = jwtTokenProvider.parseHeaders(appleSignInUpRequest.code)
        val applePublicKey = applePublicKeyGenerator.generate(applePublicKeys, tokenHeaders["kid"], tokenHeaders["alg"])
        val appleAccountId = jwtTokenProvider.getAppleTokenClaims(appleSignInUpRequest.code, applePublicKey).subject

        val signInUpDto = userService.findAppleUserOrSignUp(appleAccountId)

        val userProfile = userProfileService.findUserProfile(signInUpDto.userId)
        appleSignInUpRequest.fcmDeviceToken?.let {
            fcmTokenService.registerFcmToken(signInUpDto.userId, appleSignInUpRequest.fcmDeviceToken)
        }

        val accessToken = jwtTokenProvider.createAccessToken(signInUpDto.userId)
        val refreshToken = jwtTokenProvider.upsertRefreshToken(signInUpDto.userId)

        return AppleSignInUpResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isSignUp = signInUpDto.isSignUp,
            hasCompleteUserProfile = userProfile != null,
            hasCompleteIntroduction = userProfile?.hasCompleteIntroduction() ?: false,
        )
    }

    fun reissueToken(userId: Long, reissueTokenRequest: ReissueTokenRequest?): ReissueTokenResponse {
        val accessToken = jwtTokenProvider.createAccessToken(userId)
        val refreshToken = jwtTokenProvider.upsertRefreshToken(userId)

        reissueTokenRequest?.fcmDeviceToken?.let {
            fcmTokenService.registerFcmToken(userId, reissueTokenRequest.fcmDeviceToken)
        }

        return ReissueTokenResponse(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun smsSignUp(signUpRequest: SignUpRequest): SignUpResponse {
        val lastAuthSms = authSmsService.findLastAuthSms(signUpRequest.phoneNumber)
        lastAuthSms.validate(signUpRequest.authCode)

        val user = userService.signUp(signUpRequest)
        signUpRequest.fcmDeviceToken?.let {
            fcmTokenService.registerFcmToken(user.id, signUpRequest.fcmDeviceToken!!)
        }

        val accessToken = jwtTokenProvider.createAccessToken(user.id)
        val refreshToken = jwtTokenProvider.upsertRefreshToken(user.id)

        return SignUpResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun smsSignUpV2(signUpRequest: SignUpRequestV2): SignUpResponseV2 {
        if (signUpRequest.phoneNumber == superUserNumberV2) {
            return handleSuperUser(signUpRequest)
        }
        val lastAuthSms = authSmsService.findLastAuthSms(signUpRequest.phoneNumber)
        lastAuthSms.validate(signUpRequest.authCode)

        val user = userService.signInUpV2(signUpRequest)
        signUpRequest.fcmDeviceToken?.let {
            fcmTokenService.registerFcmToken(user.id, signUpRequest.fcmDeviceToken!!)
        }
        val userProfile = userProfileService.findUserProfile(user.id)

        val accessToken = jwtTokenProvider.createAccessToken(user.id)
        val refreshToken = jwtTokenProvider.upsertRefreshToken(user.id)

        return SignUpResponseV2(
            accessToken = accessToken,
            refreshToken = refreshToken,
            hasCompleteUserProfile = userProfile != null,
            hasCompleteIntroduction = userProfile?.hasCompleteIntroduction() ?: false,
        )
    }

    private fun handleSuperUser(signUpRequest: SignUpRequestV2): SignUpResponseV2 {
        val user = userService.findByPhoneNumber(signUpRequest.phoneNumber)!!

        if (user.deleted) {
            userService.resetUser(user.id)
            userProfileService.deleteUserProfile(user.id)
        }

        val userProfile = userProfileService.findUserProfile(user.id)
        return SignUpResponseV2(
            accessToken = jwtTokenProvider.createAccessToken(user.id),
            refreshToken = jwtTokenProvider.upsertRefreshToken(user.id),
            hasCompleteUserProfile = userProfile != null,
            hasCompleteIntroduction = userProfile?.hasCompleteIntroduction() ?: false,
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

    fun logout(userId: Long, accessToken: String, logoutRequest: LogoutRequest?) {
        blackListService.add(accessToken)
        refreshTokenService.delete(userId)
        logoutRequest?.fcmDeviceToken?.let {
            fcmTokenService.deleteFcmToken(userId, logoutRequest.fcmDeviceToken)
        }
    }

    fun delete(userId: Long, accessToken: String) {
        userService.softDelete(userId)

        applicationEventPublisher.publishEvent(
            DeleteUserEventDto(userId = userId, accessToken = accessToken)
        )
    }

    fun smsSignIn(smsSignInRequest: SmsSignInRequest): SmsSignInResponse {
        if (isSuperUser(smsSignInRequest.phoneNumber)) {
            val user = userService.findByPhoneNumberNotDeletedUser(smsSignInRequest.phoneNumber)!!

            return SmsSignInResponse(
                accessToken = jwtTokenProvider.createAccessToken(user.id),
                refreshToken = jwtTokenProvider.upsertRefreshToken(user.id),
                hasCompleteUserProfile = true,
                hasCompleteIntroduction = true,
            )
        }

        val lastAuthSms = authSmsService.findLastAuthSms(smsSignInRequest.phoneNumber)
        lastAuthSms.validate(smsSignInRequest.authCode)

        val user = userService.findByPhoneNumberNotDeletedUser(smsSignInRequest.phoneNumber)
            ?: throw IllegalArgumentException("회원가입에 대해 문의해주세요")

        val userProfile = userProfileService.findUserProfile(user.id)

        smsSignInRequest.fcmDeviceToken?.let {
            fcmTokenService.registerFcmToken(user.id, smsSignInRequest.fcmDeviceToken)
        }

        val accessToken = jwtTokenProvider.createAccessToken(user.id)
        val refreshToken = jwtTokenProvider.upsertRefreshToken(user.id)

        return SmsSignInResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            hasCompleteUserProfile = userProfile != null,
            hasCompleteIntroduction = userProfile?.hasCompleteIntroduction() ?: false,
        )
    }

    fun registerSignupProfile(userId: Long, signUpProfileRequestV2: SignUpProfileRequestV2) {
        userService.signUpProfile(
            userId,
            signUpProfileRequestV2.name,
            signUpProfileRequestV2.convertBirthDateToLocalDate(),
            signUpProfileRequestV2.gender!!,
        )
    }

    fun getAppleClientSecret(): AppleRevokeResponse {
        val clientSecret = appleAuthClientSecretKeyGenerator.generate()
        return AppleRevokeResponse(clientSecret = clientSecret)
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
private fun convertFromInternationalToKr(phoneNumber: String?): String {
    return phoneNumber
        ?.replace("+82 ", "0")
        ?.replace("-", "")
        ?: "" // 카카오 테스트 계정은 핸드폰 번호를 가져올 수 없어 조회시 null로 들어옵니다. 그러면 핸드폰 번호가 null인 애플 가입 계정과 구분할 수 없어 빈 문자열을 넣습니다.
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
