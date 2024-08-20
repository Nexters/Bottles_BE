package com.nexters.bottles.api.auth.facade.dto

data class AppleSignInUpRequest(
    val code: String,
    val fcmDeviceToken: String? = null,
) {
}
