package com.nexters.bottles.app.auth.service

import com.nexters.bottles.app.auth.domain.RefreshToken
import com.nexters.bottles.app.auth.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    @Transactional
    fun delete(userId: Long) {
        refreshTokenRepository.findAllByUserId(userId)
            .forEach { refreshTokenRepository.deleteById(it.id) }
    }

    @Transactional
    fun upsertRefreshToken(userId: Long, refreshToken: String, expiryDate: LocalDateTime) {
        val refreshTokens = refreshTokenRepository.findAllByUserId(userId)

        if (refreshTokens.isNotEmpty()) {
            refreshTokens.forEach {
                refreshTokenRepository.deleteById(it.id)
            }
            saveRefreshToken(userId, refreshToken, expiryDate)
        } else {
            saveRefreshToken(userId, refreshToken, expiryDate)
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
