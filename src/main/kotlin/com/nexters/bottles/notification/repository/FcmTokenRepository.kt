package com.nexters.bottles.notification.repository

import com.nexters.bottles.notification.domain.FcmToken
import org.springframework.data.jpa.repository.JpaRepository

interface FcmTokenRepository : JpaRepository<FcmToken, Long> {

}
