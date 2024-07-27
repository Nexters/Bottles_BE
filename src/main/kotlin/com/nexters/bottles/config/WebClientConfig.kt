package com.nexters.bottles.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    @Value("\${webclient.kakao-auth-url}")
    val kakaoAuthUrl: String
) {

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder
            .baseUrl(kakaoAuthUrl)
            .build()
    }
}
