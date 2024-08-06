package com.nexters.bottles.api.notification.repository

import com.nexters.bottles.api.notification.domain.FcmToken
import org.springframework.data.jpa.repository.JpaRepository

interface FcmTokenRepository : JpaRepository<FcmToken, Long> {

    fun findAllByUserId(userId: Long): List<FcmToken>
}
