package com.nexters.bottles.api.user.facade.dto

import com.nexters.bottles.app.user.service.dto.SignInUpStep

data class UserInfoResponse(
    val name: String?,
    val signInUpStep: SignInUpStep,
)
