package com.nexters.bottles.app.user.repository

import com.nexters.bottles.app.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByPhoneNumberAndDeletedFalse(phoneNumber: String?): User?

    fun findByIdAndDeletedFalse(id: Long): User?

    fun findAllByDeletedFalseAndIsMatchActivatedTrue(): List<User>

    fun findByPhoneNumber(phoneNumber: String?): User?

    fun findAllByPhoneNumberOrderById(phoneNumber: String): List<User>

    fun findByAppleAccountIdAndDeletedFalse(appleAccountId: String): User?
}
