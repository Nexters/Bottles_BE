package com.nexters.bottles.app.notification.service

import com.nexters.bottles.app.notification.domain.FcmToken
import com.nexters.bottles.app.notification.repository.FcmTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository
) {

    @Transactional
    fun registerFcmToken(userId: Long, token: String) {
        fcmTokenRepository.findByUserIdAndToken(userId, token) ?: run {
            val fcmToken = FcmToken(userId = userId, token = token)
            fcmTokenRepository.save(fcmToken)
        }
    }

    @Transactional
    fun deleteFcmToken(userId: Long, token: String) {
        fcmTokenRepository.findByUserIdAndToken(userId, token)?.let { fcmTokenRepository.delete(it) }
    }

    @Transactional
    fun deleteAllFcmTokenByUserId(userId: Long) {
        fcmTokenRepository.findAllByUserId(userId).forEach { fcmTokenRepository.delete(it) }
    }

    @Transactional(readOnly = true)
    fun findAllByUserIdAndTokenNotBlank(userId: Long): List<FcmToken> {
        return fcmTokenRepository.findAllByUserId(userId)
            .filterNot { it.token.isBlank() }
            .toList()
    }

    @Transactional(readOnly = true)
    fun findAllByUserIdsAndTokenNotBlank(userIds: List<Long>): List<FcmToken> {
        return fcmTokenRepository.findAllByUserIdIn(userIds)
            .filterNot { it.token.isBlank() }
            .toList()
    }

    @Transactional
    fun deleteByToken(userToken: String) {
        fcmTokenRepository.deleteByToken(userToken)
    }
}
