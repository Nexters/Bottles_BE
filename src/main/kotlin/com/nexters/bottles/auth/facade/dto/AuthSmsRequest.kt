package com.nexters.bottles.auth.facade.dto

data class AuthSmsRequest(
    val phoneNumber: String,
    val authCode: String,
) {
}
