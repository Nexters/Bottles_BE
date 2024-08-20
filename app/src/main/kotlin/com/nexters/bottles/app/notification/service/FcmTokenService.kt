package com.nexters.bottles.app.notification.service

import com.nexters.bottles.app.notification.domain.FcmToken
import com.nexters.bottles.app.notification.repository.FcmTokenRepository
import com.nexters.bottles.app.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository
) {

    @Transactional
    fun registerFcmToken(userId: Long, token: String) {
        val fcmToken = FcmToken(userId = userId, token = token)
        fcmTokenRepository.findByToken(fcmToken) ?: fcmTokenRepository.save(fcmToken)
    }

    @Transactional(readOnly = true)
    fun findByUsers(users: List<User>): List<FcmToken> {
        return users.flatMap {
            fcmTokenRepository.findAllByUserId(it.id)
        }
    }

    @Transactional
    fun deleteFcmToken(userId: Long, token: String) {
        val fcmToken = FcmToken(userId = userId, token = token)
        fcmTokenRepository.findByToken(fcmToken)?.let { fcmTokenRepository.delete(it) }
    }

    @Transactional
    fun deleteAllFcmTokenByUserId(userId: Long) {
        fcmTokenRepository.findAllByUserId(userId).forEach { fcmTokenRepository.delete(it) }
    }

    @Transactional(readOnly = true)
    fun findAllByUserId(userId: Long): List<FcmToken> {
        return fcmTokenRepository.findAllByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun findAllByUserIds(userIds: List<Long>): List<FcmToken> {
        return fcmTokenRepository.findAllByUserIdIn(userIds)
    }
}
