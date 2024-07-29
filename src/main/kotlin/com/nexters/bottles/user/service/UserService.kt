package com.nexters.bottles.user.service

import com.nexters.bottles.auth.facade.dto.KakaoUserInfoResponse
import com.nexters.bottles.user.domain.User
import com.nexters.bottles.user.domain.enum.Gender
import com.nexters.bottles.user.facade.dto.SignInUpDto
import com.nexters.bottles.user.repository.UserRepository
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
        userRepository.findByPhoneNumber(userInfoResponse.kakao_account.phone_number)?.let { user ->
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
                    )
                )
            )
            return SignInUpDto(userId = user.id, isSignUp = true)
        }
    }

    @Transactional(readOnly = true)
    fun findById(userId: Long): User {
        return userRepository.findByIdOrNull(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
    }
}
