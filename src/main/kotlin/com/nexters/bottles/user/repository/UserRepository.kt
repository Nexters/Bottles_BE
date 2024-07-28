package com.nexters.bottles.user.repository

import com.nexters.bottles.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByPhoneNumber(phoneNumber: String): User?
}
