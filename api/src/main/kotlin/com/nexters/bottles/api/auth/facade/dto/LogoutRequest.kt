package com.nexters.bottles.api.auth.facade.dto

data class LogoutRequest(
    val fcmDeviceToken: String? = null,
) {
}
