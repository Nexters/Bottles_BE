package com.nexters.bottles.api.auth.service

import com.nexters.bottles.api.auth.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    @Transactional
    fun delete(userId: Long) {
        refreshTokenRepository.findAllByUserId(userId)
            .forEach { refreshTokenRepository.deleteById(it.id) }
    }
}
