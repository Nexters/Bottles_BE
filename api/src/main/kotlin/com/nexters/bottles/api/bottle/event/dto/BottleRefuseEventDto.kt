package com.nexters.bottles.api.bottle.event.dto

data class BottleRefuseEventDto(
    val sourceUserId: Long,
    val targetUserId: Long,
    val isRefused: Boolean,
)
