package com.nexters.bottles.user.controller.dto

data class RegisterProfileRequestDto(
    val mbti: String,
    val keyword: List<String>,
    val interest: InterestDto,
    val job: String,
    var smoking: String,
    var alcohol: String,
    val religion: String,
    val region: RegionDto
) {
}
