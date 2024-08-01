package com.nexters.bottles.bottle.facade.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class BottleListResponseDto(
    val randomBottles: List<BottleDto>,
    val sentBottles: List<BottleDto>
)

data class BottleDto(
    val id: Long,
    val userName: String,
    val age: Int,
    val mbti: String?,
    val keyword: List<String>?,
    val userImageUrl: String?,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val expiredAt: LocalDateTime
)
