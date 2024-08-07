package com.nexters.bottles.api.user.facade.dto

data class SignInUpDto(
    val userId: Long,
    val isSignUp: Boolean,
) {
}
