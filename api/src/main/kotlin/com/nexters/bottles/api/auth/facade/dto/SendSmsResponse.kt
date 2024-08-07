package com.nexters.bottles.api.auth.facade.dto

import java.time.LocalDateTime

data class SendSmsResponse(
    private val expiredAt: LocalDateTime,
) {
}
