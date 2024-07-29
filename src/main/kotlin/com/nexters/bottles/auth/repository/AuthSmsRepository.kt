package com.nexters.bottles.auth.repository

import com.nexters.bottles.auth.domain.AuthSms
import org.springframework.data.jpa.repository.JpaRepository

interface AuthSmsRepository : JpaRepository<AuthSms, Long> {

    fun findByPhoneNumberOrderByIdDesc(phoneNumber: String): AuthSms?
}
