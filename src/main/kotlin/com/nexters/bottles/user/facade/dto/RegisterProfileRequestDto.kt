package com.nexters.bottles.user.facade.dto

data class RegisterProfileRequestDto(
    val mbti: String,
    val keyword: List<String>,
    val interest: InterestDto,
    val job: String,
    val height: Int,
    var smoking: String,
    var alcohol: String,
    val religion: String,
    val region: RegionDto,
    val kakaoId: String,
) {
}
