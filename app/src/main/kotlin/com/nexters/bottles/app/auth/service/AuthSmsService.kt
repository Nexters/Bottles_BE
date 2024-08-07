package com.nexters.bottles.app.auth.service

import com.nexters.bottles.app.auth.domain.AuthSms
import com.nexters.bottles.app.auth.repository.AuthSmsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthSmsService(
    private val authSmsRepository: AuthSmsRepository,
) {

    @Transactional
    fun saveAuthSms(phoneNumber: String, authCode: String, expiredAt: LocalDateTime): AuthSms {
        return authSmsRepository.save(
            AuthSms(
                phoneNumber = phoneNumber,
                authCode = authCode,
                expiredAt = expiredAt,
            )
        )
    }

    @Transactional(readOnly = true)
    fun findLastAuthSms(phoneNumber: String): AuthSms {
        return authSmsRepository.findByPhoneNumberOrderByIdDesc(phoneNumber)
            ?: throw IllegalArgumentException("다시 확인해주세요")
    }
}
