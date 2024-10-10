package com.nexters.bottles.app.user.component.event.dto

data class UploadImageEventDto(
    val prevImageUrls: List<String>,
    val prevBlurredImageUrl: String?,
) {
}
