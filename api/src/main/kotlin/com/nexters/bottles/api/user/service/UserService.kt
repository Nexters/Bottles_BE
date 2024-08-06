package com.nexters.bottles.api.user.service

import com.nexters.bottles.api.auth.facade.dto.KakaoUserInfoResponse
import com.nexters.bottles.api.auth.facade.dto.SignUpRequest
import com.nexters.bottles.api.user.domain.User
import com.nexters.bottles.api.user.domain.enum.Gender
import com.nexters.bottles.api.user.domain.enum.SignUpType
import com.nexters.bottles.api.user.facade.dto.SignInUpDto
import com.nexters.bottles.api.user.repository.UserRepository
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    private val log = KotlinLogging.logger { }

    @Transactional
    fun findUserOrSignUp(userInfoResponse: KakaoUserInfoResponse): SignInUpDto {
        userRepository.findByPhoneNumberAndDeletedFalse(userInfoResponse.kakao_account.phone_number)?.let { user ->
            log.info { "전화번호 ${userInfoResponse.kakao_account.phone_number} 유저 존재하여 조회 후 반환" }
            return SignInUpDto(userId = user.id, isSignUp = false)
        } ?: run {
            log.info { "전화번호 ${userInfoResponse.kakao_account.phone_number} 유저 존재하지 않아 회원가입" }

            val user = userRepository.save(
                User(
                    birthdate = userInfoResponse.kakao_account.birthDate
                        ?: throw IllegalArgumentException("생년월일 정보를 확인해주세요"),
                    name = userInfoResponse.kakao_account.name,
                    phoneNumber = userInfoResponse.kakao_account.phone_number,
                    gender = Gender.fromString(userInfoResponse.kakao_account.gender) ?: throw IllegalArgumentException(
                        "성별을 확인해주세요"
                    ),
                    signUpType = SignUpType.KAKAO
                )
            )
            return SignInUpDto(userId = user.id, isSignUp = true)
        }
    }

    @Transactional
    fun signUp(signUpRequest: SignUpRequest): User {
        userRepository.findByPhoneNumberAndDeletedFalse(signUpRequest.phoneNumber)?.let {
            throw IllegalArgumentException("이미 가입한 회원이에요")
        } ?: run {
            return userRepository.save(
                User(
                    birthdate = signUpRequest.convertBirthDateToLocalDate(),
                    name = signUpRequest.name,
                    phoneNumber = signUpRequest.phoneNumber,
                    gender = signUpRequest.gender,
                    signUpType = SignUpType.NORMAL
                )
            )
        }
    }

    @Transactional(readOnly = true)
    fun findByPhoneNumber(phoneNumber: String): User? {
        return userRepository.findByPhoneNumberAndDeletedFalse(phoneNumber)
    }

    @Transactional(readOnly = true)
    fun findByIdAndNotDeleted(userId: Long): User {
        return userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
    }

    @Transactional
    fun addKakaoId(userId: Long, kakaoId: String) {
        userRepository.findByIdAndDeletedFalse(userId)?.let {
            it.kakaoId = kakaoId
        } ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
    }

    @Transactional
    fun softDelete(userId: Long) {
        userRepository.findByIdOrNull(userId)?.let {
            it.softDelete()
        } ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
    }

    @Transactional(readOnly = true)
    fun findAllByNotDeleted(): List<User> {
        return userRepository.findAllByDeletedFalse()
    }
}
