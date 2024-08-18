package com.nexters.bottles.app.notification.repository

import com.nexters.bottles.app.notification.domain.FcmToken
import org.springframework.data.jpa.repository.JpaRepository

interface FcmTokenRepository : JpaRepository<FcmToken, Long> {

    fun findAllByUserId(userId: Long): List<FcmToken>

    fun findByToken(fcmToken: FcmToken): FcmToken?
}
