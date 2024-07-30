package com.nexters.bottles.auth.facade.dto

data class SmsSignInRequest(
    val phoneNumber: String,
    val authCode: String,
) {
}
