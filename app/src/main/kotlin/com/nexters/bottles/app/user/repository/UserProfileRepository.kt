package com.nexters.bottles.app.user.repository

import com.nexters.bottles.app.user.domain.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface UserProfileRepository : JpaRepository<UserProfile, Long> {

    fun findByUserId(userId: Long): UserProfile?

    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user")
    fun findAllWithUser(): List<UserProfile>

    fun findAllByCreatedAtGreaterThanAndCreatedAtLessThan(from: LocalDateTime, end: LocalDateTime): List<UserProfile>

    fun findAllByUserIdIn(userIds: List<Long>): List<UserProfile>
}
