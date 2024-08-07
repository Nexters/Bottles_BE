package com.nexters.bottles.app.auth.repository

import com.nexters.bottles.app.auth.domain.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    fun findByUserId(userId: Long): RefreshToken?

    fun findByToken(token: String): RefreshToken?

    fun findAllByUserId(userId: Long): List<RefreshToken>
}
