package com.nexters.bottles.api.auth.facade.dto

data class UpdateAppVersionResponse(
    val minimumIosVersion: Long? = null, // 이 버전 미만이면 강제 업데이트를 해야합니다
    val minimumAndroidVersion: Long? = null,
    val latestAndroidVersion: Long? = null,
) {
}
