package com.nexters.bottles.api.auth.facade.dto

data class AuthSmsRequest(
    val phoneNumber: String,
    val authCode: String,
) {
}
