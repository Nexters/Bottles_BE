package com.nexters.bottles.auth.facade.dto

data class SignUpResponse(
    val accessToken: String,
    val refreshToken: String,
)
