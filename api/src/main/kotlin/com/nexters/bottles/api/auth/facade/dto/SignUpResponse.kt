package com.nexters.bottles.api.auth.facade.dto

data class SignUpResponse(
    val accessToken: String,
    val refreshToken: String,
)
