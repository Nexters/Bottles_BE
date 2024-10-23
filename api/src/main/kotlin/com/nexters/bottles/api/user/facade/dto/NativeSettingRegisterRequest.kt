package com.nexters.bottles.api.user.facade.dto

data class NativeSettingRegisterRequest(
    val alimyTurnedOn: Boolean = false,
    val deviceName: String? = null,
    val appVersion: String? = null,
) {
}
