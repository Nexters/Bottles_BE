package com.nexters.bottles.api.auth.facade.dto

data class ReissueTokenResponse(
    val accessToken: String,
    val refreshToken: String,
) {
}
