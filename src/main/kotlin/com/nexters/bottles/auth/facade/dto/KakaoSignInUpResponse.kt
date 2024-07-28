package com.nexters.bottles.auth.facade.dto

data class KakaoSignInUpResponse(
    val accessToken: String,
    val refreshToken: String,
)
