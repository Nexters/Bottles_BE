package com.nexters.bottles.api.auth.facade.dto

data class UpdateAppVersionResponse(
    val minimumIosVersion: String? = null, // 이 버전 미만이면 강제 업데이트를 해야합니다
    val minimumAndroidVersion: String? = null,
    val latestAndroidVersion: String? = null,
) {
}
