package com.nexters.bottles.app.user.service

import com.nexters.bottles.app.common.exception.ConflictException
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.domain.enum.Gender
import com.nexters.bottles.app.user.domain.enum.SignUpType
import com.nexters.bottles.app.user.repository.UserRepository
import com.nexters.bottles.app.user.service.dto.KakaoUserInfoResponse
import com.nexters.bottles.app.user.service.dto.SignInUpDto
import com.nexters.bottles.app.user.service.dto.SignUpRequest
import com.nexters.bottles.app.user.service.dto.SignUpRequestV2
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    private val log = KotlinLogging.logger { }

    @Transactional
    fun findKakaoUserOrSignUp(userInfoResponse: KakaoUserInfoResponse): SignInUpDto {
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
            return SignInUpDto(userId = user.id, isSignUp = true, userName = userInfoResponse.kakao_account.name)
        }
    }

    @Transactional
    fun findAppleUserOrSignUp(appleAccountId: String): SignInUpDto {
        // TODO 이미 카카오 회원가입을 한 사람은 가입하지 못하게 막아야 됨 -> 프로필 등록 시 전화번호가 이미 존재하면 막기?
        userRepository.findByAppleAccountIdAndDeletedFalse(appleAccountId)?.let { user ->
            return SignInUpDto(userId = user.id, isSignUp = false)
        } ?: run {
            val user = userRepository.save(
                User(
                    signUpType = SignUpType.APPLE,
                    appleAccountId = appleAccountId
                )
            )
            return SignInUpDto(userId = user.id, isSignUp = true)
        }
    }

    @Transactional
    fun signUp(signUpRequest: SignUpRequest): User {
        userRepository.findByPhoneNumberAndDeletedFalse(signUpRequest.phoneNumber)?.let {
            throw ConflictException("이미 가입한 회원이에요")
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

    @Transactional
    fun signInUpV2(signUpRequest: SignUpRequestV2): User {
        userRepository.findByPhoneNumberAndDeletedFalse(signUpRequest.phoneNumber)?.let {
            return it
        } ?: run {
            return userRepository.save(
                User(
                    birthdate = LocalDate.now(),
                    name = "보틀",
                    phoneNumber = signUpRequest.phoneNumber,
                    signUpType = SignUpType.NORMAL
                )
            )
        }
    }

    @Transactional(readOnly = true)
    fun findByPhoneNumberNotDeletedUser(phoneNumber: String): User? {
        return userRepository.findByPhoneNumberAndDeletedFalse(phoneNumber)
    }

    @Transactional(readOnly = true)
    fun findByPhoneNumber(phoneNumber: String): User? {
        return userRepository.findByPhoneNumber(phoneNumber)
    }

    @Transactional(readOnly = true)
    fun findByIdAndNotDeleted(userId: Long): User {
        return userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
    }

    @Transactional
    fun addKakaoIdAndRegion(userId: Long, kakaoId: String, city: String, state: String) {
        userRepository.findByIdAndDeletedFalse(userId)?.let {
            it.kakaoId = kakaoId
            it.city = city
            it.state = state
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

    @Transactional
    fun updateLastActivatedAt(userId: Long, basedAt: LocalDateTime) {
        userRepository.findByIdOrNull(userId)?.let { user ->
            user.updateLastActivatedAt(basedAt)
        }
    }

    @Transactional
    fun signUpProfile(userId: Long, name: String, birthDate: LocalDate, gender: Gender) {
        userRepository.findByIdOrNull(userId)?.let { user ->
            user.name = name
            user.birthdate = birthDate
            user.gender = gender
        } ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
    }

    @Transactional
    fun resetUser(id: Long) {
        userRepository.findByIdOrNull(id)?.let {
            it.deleted = false
            it.name = "보틀"
            it.city = null
            it.state = null
            it.kakaoId = null
            it.deletedAt = null
        }
    }

    @Transactional
    fun activateMatching(userId: Long, activate: Boolean) {
        userRepository.findByIdAndDeletedFalse(userId)?.let { user ->
            user.isMatchActivated = activate
        } ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
    }
}
