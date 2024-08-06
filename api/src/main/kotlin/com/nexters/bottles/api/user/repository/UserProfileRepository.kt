package com.nexters.bottles.api.user.repository

import com.nexters.bottles.api.user.domain.UserProfile
import org.springframework.data.jpa.repository.JpaRepository

interface UserProfileRepository : JpaRepository<UserProfile, Long> {

    fun findByUserId(userId: Long): UserProfile?
}
