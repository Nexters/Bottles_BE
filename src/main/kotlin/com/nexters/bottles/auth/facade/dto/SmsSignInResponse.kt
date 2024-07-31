package com.nexters.bottles.auth.facade.dto

data class SmsSignInResponse(
    val accessToken: String,
    val refreshToken: String,
) {
}
