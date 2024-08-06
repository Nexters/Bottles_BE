package com.nexters.bottles.api.admin.facade.dto

data class ForceAfterProfileResponse(
    val accessToken1: String,
    val refreshToken1: String,
    val accessToken2: String,
    val refreshToken2: String,
) {
}
