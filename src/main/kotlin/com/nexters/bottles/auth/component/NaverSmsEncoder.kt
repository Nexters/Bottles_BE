package com.nexters.bottles.auth.component

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class NaverSmsEncoder(
    @Value("\${naver-cloud-sms.accessKey}")
    private val naverSmsAccessKey: String,

    @Value("\${naver-cloud-sms.secretKey}")
    private val naverSmsSecretKey: String,

    @Value("\${naver-cloud-sms.serviceId}")
    private val naverSmsServiceId: String,
) {

    fun generateSignature(time: Long): String {
        val newLine = "\n"
        val method = "POST"
        val url = "/sms/v2/services/$naverSmsServiceId/messages"
        val timestamp = time.toString()

        val message = StringBuilder()
            .append(method)
            .append(" ")
            .append(url)
            .append(newLine)
            .append(timestamp)
            .append(newLine)
            .append(naverSmsAccessKey)
            .toString()

        val signingKey = SecretKeySpec(naverSmsSecretKey.toByteArray(Charsets.UTF_8), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)

        val rawHmac = mac.doFinal(message.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(rawHmac)
    }
}
