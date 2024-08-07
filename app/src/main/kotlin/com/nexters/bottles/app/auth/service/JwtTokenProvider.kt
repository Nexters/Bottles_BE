package com.nexters.bottles.app.auth.service

import com.nexters.bottles.app.auth.domain.RefreshToken
import com.nexters.bottles.app.auth.repository.BlackListRepository
import com.nexters.bottles.app.auth.repository.RefreshTokenRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(
    @Value("\${jwt.access-token-secret-key}")
    private val accessTokenSecretKey: String,

    @Value("\${jwt.refresh-token-secret-key}")
    private val refreshTokenSecretKey: String,

    @Value("\${jwt.access-token-validity}")
    private val accessTokenValidityInMilliseconds: Long,

    @Value("\${jwt.refresh-token-validity}")
    private val refreshTokenValidityInMilliseconds: Long,

    private val refreshTokenRepository: RefreshTokenRepository,
    private val blackListRepository: BlackListRepository,
) {

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

    fun upsertRefreshToken(userId: Long): String {
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

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    fun getUserIdFromToken(token: String, isAccessToken: Boolean): Long? {
        val claims = getClaimsFromToken(token, isAccessToken)
        return claims?.subject?.toLongOrNull()
    }

    fun validateToken(token: String, isAccessToken: Boolean): Boolean {
        val expiredAccessToken = blackListRepository.findByExpiredAccessToken(token)
        val claims = getClaimsFromToken(token, isAccessToken)
        val now = Date()
        return expiredAccessToken == null && claims != null && !claims.expiration.before(now)
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

fun toDate(localDateTime: LocalDateTime): Date {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
}

fun toLocalDateTime(date: Date): LocalDateTime {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
}
