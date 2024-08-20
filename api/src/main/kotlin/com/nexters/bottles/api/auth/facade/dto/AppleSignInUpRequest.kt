package com.nexters.bottles.api.auth.facade.dto

data class AppleSignInUpRequest(
    val code: String, // apple identityToken
    val fcmDeviceToken: String? = null,
) {
}
