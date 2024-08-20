package com.nexters.bottles.api.auth.facade.dto

data class AppleSignInUpResponse(
    val accessToken: String,
    val refreshToken: String,
    val isSignUp: Boolean = false,
    val hasCompleteUserProfile: Boolean = false,
    val hasCompleteIntroduction: Boolean = false,
) {

}
