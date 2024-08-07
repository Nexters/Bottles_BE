package com.nexters.bottles.api.admin.component

import com.nexters.bottles.api.auth.component.toDate
import com.nexters.bottles.api.auth.domain.RefreshToken
import com.nexters.bottles.api.auth.repository.RefreshTokenRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

@Component
class TestJwtTokenProvider(
    @Value("\${jwt.access-token-secret-key}")
    private val accessTokenSecretKey: String,

    @Value("\${jwt.refresh-token-secret-key}")
    private val refreshTokenSecretKey: String,

    private val refreshTokenRepository: RefreshTokenRepository,
) {

    private val accessKey = Keys.hmacShaKeyFor(accessTokenSecretKey.toByteArray())
    private val refreshKey = Keys.hmacShaKeyFor(refreshTokenSecretKey.toByteArray())

    fun createAccessToken(
        userId: Long,
        accessTokenValidityInMilliseconds: Long
    ): String {
        val now = LocalDateTime.now()
        val expiryDate = now.plus(Duration.ofMillis(accessTokenValidityInMilliseconds))

        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(toDate(now))
            .setExpiration(toDate(expiryDate))
            .signWith(accessKey)
            .compact()
    }

    fun upsertRefreshToken(userId: Long, refreshTokenValidityInMilliseconds: Long): String {
        val now = LocalDateTime.now()
        val expiryDate = now.plus(Duration.ofMillis(refreshTokenValidityInMilliseconds))

        val token = Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(toDate(now))
            .setExpiration(toDate(expiryDate))
            .signWith(refreshKey)
            .compact()

        upsertRefreshToken(userId = userId, refreshToken = token, expiryDate = expiryDate)

        return token
    }

    private fun upsertRefreshToken(userId: Long, refreshToken: String, expiryDate: LocalDateTime) {
        val refreshTokens = refreshTokenRepository.findAllByUserId(userId)

        if (refreshTokens.isNotEmpty()) {
            refreshTokens.forEach {
                refreshTokenRepository.deleteById(it.id)
            }
            refreshTokenRepository.save(
                RefreshToken(
                    userId = userId,
                    token = refreshToken,
                    expiryDate = expiryDate
                )
            )
        } else {
            refreshTokenRepository.save(
                RefreshToken(
                    userId = userId,
                    token = refreshToken,
                    expiryDate = expiryDate
                )
            )
        }
    }
}
