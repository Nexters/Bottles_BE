package com.nexters.bottles.app.user.repository

import com.nexters.bottles.app.user.domain.UserAlimy
import com.nexters.bottles.app.user.domain.enum.AlimyType
import org.springframework.data.jpa.repository.JpaRepository

interface UserAlimyRepository: JpaRepository<UserAlimy, Long> {

    fun findByUserIdAndAlimyType(userId: Long, alimyType: AlimyType): UserAlimy?

    fun findAllByUserId(userId: Long): List<UserAlimy>
}
