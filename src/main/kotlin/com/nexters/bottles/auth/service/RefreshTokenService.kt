package com.nexters.bottles.auth.service

import com.nexters.bottles.auth.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    @Transactional
    fun delete(userId: Long) {
        refreshTokenRepository.findAllByUserId()
            .forEach { refreshTokenRepository.deleteById(it.id) }
    }
}
