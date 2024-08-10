package com.nexters.bottles.api.user.component.event.dto

import java.time.LocalDateTime

data class UserApplicationEventDto(
    val userId: Long,
    val basedAt: LocalDateTime,
) {
}
