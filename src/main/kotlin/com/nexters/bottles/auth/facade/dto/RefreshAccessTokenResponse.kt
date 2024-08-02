package com.nexters.bottles.auth.facade.dto

data class RefreshAccessTokenResponse(
    val accessToken: String,
    val refreshToken: String,
) {
}
