package com.nexters.bottles.app.auth.repository

import com.nexters.bottles.app.auth.domain.AuthSms
import org.springframework.data.jpa.repository.JpaRepository

interface AuthSmsRepository : JpaRepository<AuthSms, Long> {

    fun findFirstByPhoneNumberOrderByIdDesc(phoneNumber: String): AuthSms?
}
