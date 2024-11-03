package com.nexters.bottles.api.bottle.event.dto

data class BottleMatchEventDto(
    val bottleId: Long,
    val sourceUserId: Long,
    val targetUserId: Long
) {

}
