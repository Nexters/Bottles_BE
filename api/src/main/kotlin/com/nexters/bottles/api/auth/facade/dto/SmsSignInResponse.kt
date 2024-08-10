package com.nexters.bottles.api.auth.facade.dto

data class SmsSignInResponse(
    val accessToken: String,
    val refreshToken: String,
    val hasCompleteUserProfile: Boolean = false,
    val hasCompleteIntroduction: Boolean = false,
) {
}
