package com.nexters.bottles.app.notification.repository

import com.nexters.bottles.app.notification.domain.FcmToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FcmTokenRepository : JpaRepository<FcmToken, Long> {

    fun findAllByUserId(userId: Long): List<FcmToken>

    fun findByUserIdAndToken(userId: Long, token: String): FcmToken?

    fun findAllByUserIdIn(@Param("userIds") userIds: List<Long>): List<FcmToken>

    fun deleteByToken(userToken: String)
}
