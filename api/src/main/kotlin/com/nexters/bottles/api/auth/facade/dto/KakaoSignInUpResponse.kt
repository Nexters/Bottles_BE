package com.nexters.bottles.api.auth.facade.dto

data class KakaoSignInUpResponse(
    val accessToken: String,
    val refreshToken: String,
    val isSignUp: Boolean = false,
    val hasUserProfile: Boolean = false,
)
