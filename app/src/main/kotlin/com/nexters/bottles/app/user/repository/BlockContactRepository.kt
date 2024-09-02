package com.nexters.bottles.app.user.repository

import com.nexters.bottles.app.user.domain.BlockContact
import org.springframework.data.jpa.repository.JpaRepository

interface BlockContactRepository : JpaRepository<BlockContact, Long> {

    fun findAllByUserId(userId: Long): List<BlockContact>

    fun findAllByPhoneNumber(phoneNumber: String): List<BlockContact>
}
