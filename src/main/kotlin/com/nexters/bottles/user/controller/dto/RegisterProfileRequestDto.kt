package com.nexters.bottles.user.controller.dto

data class RegisterProfileRequestDto(
    val mbti: String,
    val keyword: List<String>,
    val interest: InterestDto,
    val job: String,
    val smoking: String,
    val alcohol: String,
    val religion: String,
    val region: RegionDto
)
