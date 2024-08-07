package com.nexters.bottles.app.user.repository

import com.nexters.bottles.app.user.domain.UserProfile
import org.springframework.data.jpa.repository.JpaRepository

interface UserProfileRepository : JpaRepository<UserProfile, Long> {

    fun findByUserId(userId: Long): UserProfile?
}
