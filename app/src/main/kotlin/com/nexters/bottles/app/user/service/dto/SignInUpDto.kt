package com.nexters.bottles.app.user.service.dto

data class SignInUpDto(
    val userId: Long,
    val isSignUp: Boolean,
    val userName: String? = "보틀",
) {
}
