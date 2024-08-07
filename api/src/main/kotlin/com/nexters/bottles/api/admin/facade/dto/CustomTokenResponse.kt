package com.nexters.bottles.api.admin.facade.dto

data class CustomTokenResponse(
    val accessToken: String,
    val refreshToken: String
)
