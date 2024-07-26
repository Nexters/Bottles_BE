package com.nexters.bottles.auth.component

import com.nexters.bottles.auth.domain.RefreshToken
import com.nexters.bottles.auth.repository.RefreshTokenRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.time.Duration

@Component
class JwtTokenProvider(
    @Value("\${jwt.access-token-secret-key}")
    private val accessTokenSecretKey: String,

    @Value("\${jwt.refresh-token-secret-key}")
    private val refreshTokenSecretKey: String,

    private val refreshTokenRepository: RefreshTokenRepository,
) {

    private val accessTokenValidityInMilliseconds  = 1000 * 60 * 60 * 10L // 10시간
    private val refreshTokenValidityInMilliseconds = 1000 * 60 * 60 * 24 * 7L // 7일
    private val accessKey = Keys.hmacShaKeyFor(accessTokenSecretKey.toByteArray())
    private val refreshKey = Keys.hmacShaKeyFor(refreshTokenSecretKey.toByteArray())

    fun createAccessToken(userId: Long): String {
        val now = LocalDateTime.now()
        val expiryDate = now.plus(Duration.ofMillis(accessTokenValidityInMilliseconds))

        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(toDate(now))
            .setExpiration(toDate(expiryDate))
            .signWith(accessKey)
            .compact()
    }

    fun createRefreshToken(userId: Long): String {
        val now = LocalDateTime.now()
        val expiryDate = now.plus(Duration.ofMillis(refreshTokenValidityInMilliseconds))

        val token = Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(toDate(now))
            .setExpiration(toDate(expiryDate))
            .signWith(refreshKey)
            .compact()

        val refreshToken = RefreshToken(
            userId = userId,
            token = token,
            expiryDate = expiryDate
        )
        refreshTokenRepository.save(refreshToken)

        return token
    }

    fun getUserIdFromToken(token: String, isAccessToken: Boolean): Long? {
        val claims = getClaimsFromToken(token, isAccessToken)
        return claims?.subject?.toLongOrNull()
    }

    fun validateToken(token: String, isAccessToken: Boolean): Boolean {
        val claims = getClaimsFromToken(token, isAccessToken)
        val now = Date()
        return claims != null && !claims.expiration.before(now)
    }

    fun validateRefreshToken(token: String): Boolean {
        val refreshToken = refreshTokenRepository.findByToken(token)
        val now = LocalDateTime.now()
        return refreshToken != null && !now.isAfter(refreshToken.expiryDate)
    }

    private fun getClaimsFromToken(token: String, isAccessToken: Boolean): Claims? {
        return try {
            val parser = Jwts.parserBuilder()
                .setSigningKey(
                    if (isAccessToken) accessTokenSecretKey.toByteArray()
                    else refreshTokenSecretKey.toByteArray()
                ).build()
            parser.parseClaimsJws(token).body
        } catch (e: Exception) {
            null
        }
    }
}

fun toDate(localDateTime: LocalDateTime): Date {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
}

fun toLocalDateTime(date: Date): LocalDateTime {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
}
