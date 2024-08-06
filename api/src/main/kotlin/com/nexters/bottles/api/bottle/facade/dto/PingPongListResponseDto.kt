package com.nexters.bottles.api.bottle.facade.dto

data class PingPongListResponseDto(
    val activeBottles: List<PingPongBottleDto>,
    val doneBottles: List<PingPongBottleDto>
)

data class PingPongBottleDto(
    val id: Long,
    val isRead: Boolean,
    val userName: String,
    val age: Int,
    val mbti: String?,
    val keyword: List<String>?,
    val userImageUrl: String?,
)
