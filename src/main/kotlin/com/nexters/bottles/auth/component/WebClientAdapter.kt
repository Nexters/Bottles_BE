package com.nexters.bottles.auth.component

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import com.nexters.bottles.auth.facade.dto.KakaoUserInfoResponse

@Component
class WebClientAdapter(
    private val webClient: WebClient,
) {

    fun sendAuthRequest(code: String): KakaoUserInfoResponse? {

        return webClient.get()
            .uri("/v2/user/me")
            .header("Authorization", "Bearer $code")
            .retrieve()
            .bodyToMono(KakaoUserInfoResponse::class.java)
            .block()
    }
}
