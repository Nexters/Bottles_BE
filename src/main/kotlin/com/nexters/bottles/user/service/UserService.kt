package com.nexters.bottles.user.service

import com.nexters.bottles.auth.facade.dto.KakaoUserInfoResponse
import com.nexters.bottles.user.domain.User
import com.nexters.bottles.user.domain.enum.Gender
import com.nexters.bottles.user.repository.UserRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    private val log = KotlinLogging.logger { }

    @Transactional
    fun findUserOrSignUp(userInfoResponse: KakaoUserInfoResponse): User {
        userRepository.findByPhoneNumber(userInfoResponse.kakao_account.phone_number)?.let {user ->
            log.info { "전화번호 ${userInfoResponse.kakao_account.phone_number} 유저 존재하여 조회 후 반환" }
            return user
        } ?: run {
            log.info { "전화번호 ${userInfoResponse.kakao_account.phone_number} 유저 존재하지 않아 회원가입" }

            return userRepository.save(
                User(
                    birthdate = userInfoResponse.kakao_account.birthDate ?: throw IllegalArgumentException("생년월일 정보를 확인해주세요"),
                    name = userInfoResponse.kakao_account.name,
                    phoneNumber = userInfoResponse.kakao_account.phone_number,
                    gender = Gender.fromString(userInfoResponse.kakao_account.gender) ?: throw IllegalArgumentException("성별을 확인해주세요")
                )
            )
        }
    }
}
