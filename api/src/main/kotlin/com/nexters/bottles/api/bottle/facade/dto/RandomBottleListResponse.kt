package com.nexters.bottles.api.bottle.facade.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class RandomBottleListResponse(
    val randomBottles: List<RandomBottleDto>,
    val nextBottleLeftHours: Int,
)

data class RandomBottleDto(
    val id: Long,
    val userId: Long,
    val userName: String?,
    val age: Int,
    val mbti: String?,
    val keyword: List<String>?,
    val userImageUrl: String?,
    val lastActivatedAt: String?,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val expiredAt: LocalDateTime
)
