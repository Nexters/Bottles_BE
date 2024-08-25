package com.nexters.bottles.app.notification.repository

import com.nexters.bottles.app.notification.domain.FcmToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FcmTokenRepository : JpaRepository<FcmToken, Long> {

    @Query(
        value = "SELECT ft FROM FcmToken ft " +
                "WHERE ft.token != '' "
    )
    fun findAllByUserIdAndTokenNotBlank(userId: Long): List<FcmToken>

    fun findAllByUserId(userId: Long): List<FcmToken>

    fun findByUserIdAndToken(userId: Long, token: String): FcmToken?

    @Query(
        value = "SELECT ft FROM FcmToken ft " +
                "WHERE ft.token != '' AND ft.userId IN (:userIds)"
    )
    fun findAllByUserIdInAndTokenNotBlank(@Param("userIds") userIds: List<Long>): List<FcmToken>
}
