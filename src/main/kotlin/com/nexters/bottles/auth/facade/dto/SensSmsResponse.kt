package com.nexters.bottles.auth.facade.dto

import java.time.LocalDateTime

data class SensSmsResponse(
    private val expiredAt: LocalDateTime,
) {
}
