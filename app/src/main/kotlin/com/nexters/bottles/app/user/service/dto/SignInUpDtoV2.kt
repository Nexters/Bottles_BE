package com.nexters.bottles.app.user.service.dto

data class SignInUpDtoV2(
    val userId: Long,
    val signInUpStep: SignInUpStep,
) {
}
