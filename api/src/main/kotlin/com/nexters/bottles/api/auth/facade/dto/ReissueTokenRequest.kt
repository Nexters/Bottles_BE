package com.nexters.bottles.api.auth.facade.dto

data class ReissueTokenRequest(
    val fcmDeviceToken: String? = null,
) {
}
