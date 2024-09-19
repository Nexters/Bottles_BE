package com.nexters.bottles.app.user.repository

import com.nexters.bottles.app.user.domain.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserProfileRepository : JpaRepository<UserProfile, Long> {

    fun findByUserId(userId: Long): UserProfile?

    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user")
    fun findAllWithUser(): List<UserProfile>
}
