package com.nexters.bottles.notification.service

import com.nexters.bottles.notification.domain.FcmToken
import com.nexters.bottles.notification.repository.FcmTokenRepository
import com.nexters.bottles.user.domain.User
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
}
