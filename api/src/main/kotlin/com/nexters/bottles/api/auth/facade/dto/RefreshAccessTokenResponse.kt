package com.nexters.bottles.api.auth.facade.dto

data class RefreshAccessTokenResponse(
    val accessToken: String,
    val refreshToken: String,
) {
}
