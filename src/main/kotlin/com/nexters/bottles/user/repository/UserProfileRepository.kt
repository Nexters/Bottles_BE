package com.nexters.bottles.user.repository

import com.nexters.bottles.user.domain.UserProfile
import org.springframework.data.jpa.repository.JpaRepository

interface UserProfileRepository : JpaRepository<UserProfile, Long>
