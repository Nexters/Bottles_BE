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
    fun registerFcmToken(user: User, token: String) {
        val fcmToken = FcmToken(userId = user.id, token = token)
        fcmTokenRepository.save(fcmToken)
    }

    @Transactional(readOnly = true)
    fun findByUsers(users: List<User>): List<FcmToken> {
        return users.flatMap {
            fcmTokenRepository.findAllByUserId(it.id)
        }
    }
}
