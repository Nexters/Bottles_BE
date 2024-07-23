package com.nexters.bottles.bottle.facade.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class BottleDto(
    val id: Long,
    val userName: String,
    val age: Int,
    val mbti: String?,
    val keyword: List<String>?,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val expiredAt: LocalDateTime
)
