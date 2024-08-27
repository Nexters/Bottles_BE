package com.nexters.bottles.api.admin.facade.dto

class PushMessageRequest(
    val userIds: List<Long>,
    val title: String,
    val body: String,
) {
}
