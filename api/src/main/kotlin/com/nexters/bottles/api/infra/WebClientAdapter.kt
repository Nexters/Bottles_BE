package com.nexters.bottles.api.infra

import com.nexters.bottles.api.auth.facade.dto.KakaoUserInfoResponse
import com.nexters.bottles.api.auth.facade.dto.MessageDTO
import com.nexters.bottles.api.auth.facade.dto.SmsResponseDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Component
class WebClientAdapter(
    @Value("\${webclient.kakao-auth-url}")
    val kakaoAuthUrl: String,

    @Value("\${naver-cloud-sms.accessKey}")
    val naverSmsAccessKey: String,

    @Value("\${naver-cloud-sms.serviceId}")
    val naverSmsServiceId: String,

    @Value("\${naver-cloud-sms.senderPhone}")
    val naverSmsSenderPhone: String,
) {

    fun sendAuthRequest(code: String): KakaoUserInfoResponse {
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

    fun sendSms(time: Long, messageDto: MessageDTO, signature: String): SmsResponseDTO? {
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

        val request = com.nexters.bottles.api.auth.facade.dto.SmsRequestDTO(
            type = "SMS",
            contentType = "COMM",
            countryCode = "82",
            from = naverSmsSenderPhone,
            content = messageDto.content,
            messages = listOf(messageDto)
        )

        val response: SmsResponseDTO? = webClient.post()
            .uri("/sms/v2/services/$naverSmsServiceId/messages")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(SmsResponseDTO::class.java)
            .block()

        return response
    }
}
