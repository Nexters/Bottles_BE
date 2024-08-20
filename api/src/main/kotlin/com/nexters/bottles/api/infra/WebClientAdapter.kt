package com.nexters.bottles.api.infra

import com.nexters.bottles.api.auth.facade.dto.ApplePublicKeys
import com.nexters.bottles.api.auth.facade.dto.AppleUserInfoResponse
import com.nexters.bottles.api.auth.facade.dto.MessageDto
import com.nexters.bottles.api.auth.facade.dto.SmsRequest
import com.nexters.bottles.api.auth.facade.dto.SmsResponse
import com.nexters.bottles.app.user.service.dto.KakaoUserInfoResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Component
class WebClientAdapter(
    @Value("\${webclient.kakao-auth-url}")
    val kakaoAuthUrl: String,

    @Value("\${apple-auth.apple-url}")
    val appleAuthUrl: String,

    @Value("\${apple-auth.apple-client-id}")
    val appleClientId: String,

    @Value("\${naver-cloud-sms.accessKey}")
    val naverSmsAccessKey: String,

    @Value("\${naver-cloud-sms.serviceId}")
    val naverSmsServiceId: String,

    @Value("\${naver-cloud-sms.senderPhone}")
    val naverSmsSenderPhone: String,
) {

    fun sendKakaoAuthRequest(code: String): KakaoUserInfoResponse {
        val webClient = WebClient.builder()
            .baseUrl(kakaoAuthUrl)
            .build()

        return webClient.get()
            .uri("/v2/user/me")
            .header("Authorization", "Bearer $code")
            .retrieve()
            .bodyToMono(KakaoUserInfoResponse::class.java)
            .block() ?: throw IllegalArgumentException("회원가입에 대해 문의해주세요")
    }

    fun sendAppleAuthKeysRequest(): ApplePublicKeys {
        val webClient = WebClient.builder()
            .baseUrl(appleAuthUrl)
            .build()

        return webClient.get()
            .uri("/auth/keys")
            .retrieve()
            .bodyToMono(ApplePublicKeys::class.java)
            .block() ?: throw IllegalArgumentException("회원가입에 대해 문의해주세요")
    }

    fun sendAppleAuthRequest(clientSecretKey: String, code: String): AppleUserInfoResponse {
        val webClient = WebClient.builder()
            .baseUrl(appleAuthUrl)
            .build()

        return webClient.post()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/auth/token")
                    .queryParam("grant_type", "authorization_code")
                    .queryParam("client_id", appleClientId)
                    .queryParam("client_secret", clientSecretKey)
                    .queryParam("code", code)
                    .build()
            }
            .retrieve()
            .bodyToMono(AppleUserInfoResponse::class.java)
            .block() ?: throw IllegalArgumentException("회원가입에 대해 문의해주세요")
    }

    fun sendSms(time: Long, messageDto: MessageDto, signature: String): SmsResponse? {
        val headers = mapOf(
            "Content-Type" to MediaType.APPLICATION_JSON_VALUE,
            "x-ncp-apigw-timestamp" to time.toString(),
            "x-ncp-iam-access-key" to naverSmsAccessKey,
            "x-ncp-apigw-signature-v2" to signature
        )

        val webClient = WebClient.builder()
            .baseUrl("https://sens.apigw.ntruss.com")
            .defaultHeaders { httpHeaders ->
                headers.forEach { (key, value) ->
                    httpHeaders.add(key, value)
                }
            }
            .build()

        val request = SmsRequest(
            type = "SMS",
            contentType = "COMM",
            countryCode = "82",
            from = naverSmsSenderPhone,
            content = messageDto.content,
            messages = listOf(messageDto)
        )

        val response: SmsResponse? = webClient.post()
            .uri("/sms/v2/services/$naverSmsServiceId/messages")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(SmsResponse::class.java)
            .block()

        return response
    }
}
