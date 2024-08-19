package com.nexters.bottles.api.auth.facade.dto

import com.nexters.bottles.app.user.service.dto.SignInUpStep

data class SignUpResponseV2(
    val accessToken: String,
    val refreshToken: String,
    val hasCompleteUserProfile: Boolean,
    val hasCompleteIntroduction: Boolean,
)
