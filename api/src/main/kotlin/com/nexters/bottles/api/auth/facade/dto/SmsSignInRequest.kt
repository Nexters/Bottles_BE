package com.nexters.bottles.api.auth.facade.dto

data class SmsSignInRequest(
    val phoneNumber: String,
    val authCode: String,
) {
}
