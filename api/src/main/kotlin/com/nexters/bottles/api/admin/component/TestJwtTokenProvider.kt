package com.nexters.bottles.api.admin.component

import com.nexters.bottles.api.auth.component.toDate
import com.nexters.bottles.app.auth.service.RefreshTokenService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Component
class TestJwtTokenProvider(
    @Value("\${jwt.access-token-secret-key}")
    private val accessTokenSecretKey: String,

    @Value("\${jwt.refresh-token-secret-key}")
    private val refreshTokenSecretKey: String,

    private val refreshTokenService: RefreshTokenService,
) {

    private val accessKey = Keys.hmacShaKeyFor(accessTokenSecretKey.toByteArray())
    private val refreshKey = Keys.hmacShaKeyFor(refreshTokenSecretKey.toByteArray())

    fun createAccessToken(
        userId: Long,
        accessTokenValidityInMilliseconds: Long
    ): String {
        val nowLocalDateTime = LocalDateTime.now()
        val now = LocalDateTime.of(
            LocalDate.now(),
            LocalTime.of(nowLocalDateTime.hour, nowLocalDateTime.minute)
        )
        val expiryDate = now.plus(Duration.ofMillis(accessTokenValidityInMilliseconds))

        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(toDate(now))
            .setExpiration(toDate(expiryDate))
            .signWith(accessKey)
            .compact()
    }

    fun upsertRefreshToken(userId: Long, refreshTokenValidityInMilliseconds: Long): String {
        val nowLocalDateTime = LocalDateTime.now()
        val now = LocalDateTime.of(
            LocalDate.now(),
            LocalTime.of(nowLocalDateTime.hour, nowLocalDateTime.minute)
        )
        val expiryDate = now.plus(Duration.ofMillis(refreshTokenValidityInMilliseconds))

        val token = Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(toDate(now))
            .setExpiration(toDate(expiryDate))
            .signWith(refreshKey)
            .compact()

        refreshTokenService.upsertRefreshToken(userId = userId, refreshToken = token, expiryDate = expiryDate)

        return token
    }
}
