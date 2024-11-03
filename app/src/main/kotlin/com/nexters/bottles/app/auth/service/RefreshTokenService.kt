package com.nexters.bottles.app.auth.service

import com.nexters.bottles.app.auth.domain.RefreshToken
import com.nexters.bottles.app.auth.repository.RefreshTokenRepository
import mu.KotlinLogging
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    private val log = KotlinLogging.logger {  }

    @Transactional
    fun delete(userId: Long) {
        refreshTokenRepository.findAllByUserId(userId)
            .forEach { refreshTokenRepository.deleteById(it.id) }
    }

    @Transactional
    fun upsertRefreshToken(userId: Long, refreshToken: String, expiryDate: LocalDateTime) {
        try {
            val refreshTokens = refreshTokenRepository.findAllByUserId(userId)

            if (refreshTokens.isNotEmpty()) {
                refreshTokenRepository.deleteAllInBatch(refreshTokens)
                saveRefreshToken(userId, refreshToken, expiryDate)
            } else {
                saveRefreshToken(userId, refreshToken, expiryDate)
            }
        } catch (e: ObjectOptimisticLockingFailureException) {
            log.warn { "리프레시 토큰 삭제에서 예외 발생"}
        }
    }

    private fun saveRefreshToken(userId: Long, refreshToken: String, expiryDate: LocalDateTime) {
        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                token = refreshToken,
                expiryDate = expiryDate
            )
        )
    }
}
