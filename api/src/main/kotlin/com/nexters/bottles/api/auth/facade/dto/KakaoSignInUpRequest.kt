package com.nexters.bottles.api.auth.facade.dto

class KakaoSignInUpRequest(
    val code: String,
    val fcmDeviceToken: String? = null,
)
