package com.nexters.bottles.auth.repository

import com.nexters.bottles.auth.domain.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    fun findByUserId(userId: Long): RefreshToken?

    fun findByToken(token: String): RefreshToken?
}
