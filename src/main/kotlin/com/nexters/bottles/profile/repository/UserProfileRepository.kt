package com.nexters.bottles.profile.repository

import com.nexters.bottles.profile.domain.UserProfile
import org.springframework.data.jpa.repository.JpaRepository

interface UserProfileRepository : JpaRepository<UserProfile, Long>
