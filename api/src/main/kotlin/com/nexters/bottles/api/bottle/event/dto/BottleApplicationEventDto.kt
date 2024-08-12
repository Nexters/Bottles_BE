package com.nexters.bottles.api.bottle.event.dto

data class BottleApplicationEventDto(
    val sourceUserId: Long,
    val targetUserId: Long,
    val isRefused: Boolean,
)
