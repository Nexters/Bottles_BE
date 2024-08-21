package com.nexters.bottles.api.auth.component

import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.StringReader
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.time.LocalDateTime


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

        val reader = StringReader(keyPath)
        val pemParser = PEMParser(reader)
        val jcaPEMKeyConverter = JcaPEMKeyConverter()
        val privateKeyInfo = pemParser.readObject() as PrivateKeyInfo
        val privateKey = jcaPEMKeyConverter.getPrivateKey(privateKeyInfo)

        val now = LocalDateTime.now()
        val expiryDate = now.plus(Duration.ofMillis(EXPIRATION_MILLIS))
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

    companion object {
        const val EXPIRATION_MILLIS = (1000 * 60 * 6).toLong() // 6시간
    }
}

