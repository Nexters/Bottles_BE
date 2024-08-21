package com.nexters.bottles.api.auth.component

import com.nexters.bottles.api.auth.facade.dto.ApplePublicKeys
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Component
class ApplePublicKeyGenerator {

    fun generate(applePublicKeys: ApplePublicKeys, kid: String?, alg: String?): PublicKey {
        val applePublicKey = applePublicKeys.keys
            .findLast { key -> key.kid == kid && key.alg == alg }
            ?: throw IllegalArgumentException("고객센터에 문의해주세요")

        val nBytes = Base64.getUrlDecoder().decode(applePublicKey.n)
        val eBytes = Base64.getUrlDecoder().decode(applePublicKey.e)

        val publicKeySpec = RSAPublicKeySpec(
            BigInteger(1, nBytes),
            BigInteger(1, eBytes)
        )

        val keyFactory = KeyFactory.getInstance(applePublicKey.kty)
        val publicKey = keyFactory.generatePublic(publicKeySpec)

        return publicKey
    }
}
