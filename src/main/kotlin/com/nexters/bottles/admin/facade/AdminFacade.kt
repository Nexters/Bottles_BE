package com.nexters.bottles.admin.facade

import com.nexters.bottles.admin.component.TestJwtTokenProvider
import com.nexters.bottles.admin.facade.dto.CreateCustomTokenRequest
import com.nexters.bottles.admin.facade.dto.CustomTokenResponse
import org.springframework.stereotype.Component

@Component
class AdminFacade(
    private val testJwtTokenProvider: TestJwtTokenProvider,
) {

    fun createCustomValidityToken(
        userId: Long,
        createCustomTokenRequest: CreateCustomTokenRequest
    ): CustomTokenResponse {
        val accessTokenValidityInMilliseconds = createCustomTokenRequest.accessTime * 1000
        val refreshTokenValidityInMilliseconds = createCustomTokenRequest.refreshTime * 1000

        val accessToken = testJwtTokenProvider.createAccessToken(userId, accessTokenValidityInMilliseconds)
        val refreshToken = testJwtTokenProvider.upsertRefreshToken(userId, refreshTokenValidityInMilliseconds)

        return CustomTokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}
