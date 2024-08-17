package com.nexters.bottles.api.auth.facade.dto

data class LogoutRequest(
    val test: String,
    val fcmDeviceToken: String? = null,
) {
}
