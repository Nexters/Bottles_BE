package com.nexters.bottles.app.user.service.dto

data class SignUpRequestV2(
    val authCode: String,
    val phoneNumber: String,
    val fcmDeviceToken: String? = null,
) {
}
