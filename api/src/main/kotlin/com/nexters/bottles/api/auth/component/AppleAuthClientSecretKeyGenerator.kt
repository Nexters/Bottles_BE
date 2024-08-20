package com.nexters.bottles.api.auth.component

import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


@Component
class AppleAuthClientSecretKeyGenerator(
    @Value("\${apple-auth.apple-url}")
    val appleUrl: String,

    @Value("\${apple-auth.apple-key-id}")
    val appleKeyId: String,

    @Value("\${apple-auth.apple-key-id-path}")
    val appleKeyIdPath: String,

    @Value("\${apple-auth.apple-client-id}")
    val appleClientId: String,

    @Value("\${apple-auth.apple-team-id}")
    val appleTeamId: String,
) {

    fun generate(): String {
        val path = Paths.get(appleKeyIdPath)
        val keyPath = Files.newBufferedReader(path).use { bufferedReader ->
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { line ->
                stringBuilder.append(line)
                stringBuilder.append("\n")
            }
            stringBuilder.toString()
        }

        val privateKeyPEM = keyPath
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s+".toRegex(), "")

        val decodedKey = Base64.getDecoder().decode(privateKeyPEM)
        val keySpec = PKCS8EncodedKeySpec(decodedKey)
        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKey = keyFactory.generatePrivate(keySpec)

        val now = LocalDateTime.now()
        val expiryDate = now.plus(Duration.ofMillis(1000 * 120))
        return Jwts
            .builder()
            .setHeaderParam(JwsHeader.KEY_ID, appleKeyId)
            .setIssuer(appleTeamId)
            .setAudience(appleUrl)
            .setSubject(appleClientId)
            .setIssuedAt(toDate(now))
            .setExpiration(toDate(expiryDate))
            .signWith(privateKey, SignatureAlgorithm.ES256)
            .compact()
    }
}

