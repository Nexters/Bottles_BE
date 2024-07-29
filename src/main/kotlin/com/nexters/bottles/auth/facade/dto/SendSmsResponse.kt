package com.nexters.bottles.auth.facade.dto

import java.time.LocalDateTime

data class SendSmsResponse(
    private val expiredAt: LocalDateTime,
) {
}
