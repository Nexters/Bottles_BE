package com.nexters.bottles.api.auth.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nexters.bottles.app.auth.domain.BlackList
import com.nexters.bottles.app.auth.service.BlackListService
import com.nexters.bottles.app.auth.service.RefreshTokenService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.PublicKey
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

    private val refreshTokenService: RefreshTokenService,
    private val blackListService: BlackListService,
    private val objectMapper: ObjectMapper,
) {

    private val accessKey = Keys.hmacShaKeyFor(accessTokenSecretKey.toByteArray())
    private val refreshKey = Keys.hmacShaKeyFor(refreshTokenSecretKey.toByteArray())
    private val log = KotlinLogging.logger {}

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

        refreshTokenService.upsertRefreshToken(userId = userId, refreshToken = token, expiryDate = expiryDate)

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
        var expiredAccessToken = blackListService.findLastExpiredToken(token)
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

    fun parseHeaders(token: String): Map<String, String> {
        val header = token.split(".")[0]
        val decoder = Base64.getUrlDecoder()
        val decodedHeader = String(decoder.decode(header))
        return try {
            objectMapper.readValue(decodedHeader)
        } catch (e: Exception) {
            throw RuntimeException("Error decoding token payload", e)
        }
    }

    fun getAppleTokenClaims(token: String, publicKey: PublicKey): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}

fun toDate(localDateTime: LocalDateTime): Date {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
}

fun toLocalDateTime(date: Date): LocalDateTime {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
}

