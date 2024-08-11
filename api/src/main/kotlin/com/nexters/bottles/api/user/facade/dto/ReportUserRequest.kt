package com.nexters.bottles.api.user.facade.dto

data class ReportUserRequest(
    val userId: Long,
    val reportReasonShortAnswer: String,
) {
}
