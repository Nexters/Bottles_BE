package com.nexters.bottles.bottle.facade.dto

data class BottleDto(
    val id: Long,
    val userName: String,
    val age: Int,
    val mbti: String,
    val keyword: List<String>,
    val validTime: Int
)
