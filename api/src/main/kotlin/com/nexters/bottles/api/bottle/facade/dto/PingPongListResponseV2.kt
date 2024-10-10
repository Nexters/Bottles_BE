package com.nexters.bottles.api.bottle.facade.dto

import com.nexters.bottles.app.bottle.domain.enum.LetterLastStatus

data class PingPongListResponseV2(
    val pingPongBottles: List<PingPongBottleDtoV2>,
)

data class PingPongBottleDtoV2(
    val id: Long,
    val isRead: Boolean,
    val userName: String?,
    val userId: Long,
    val age: Int,
    val mbti: String?,
    val keyword: List<String>?,
    val userImageUrl: String?,
    val lastActivatedAt: String?,
    val lastStatus: LetterLastStatus
)
