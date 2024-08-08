package com.nexters.bottles.api.auth.facade.dto

data class SmsSignInResponse(
    val accessToken: String,
    val refreshToken: String,
    val hasUserProfile: Boolean = false,
) {
}
