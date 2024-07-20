package com.nexters.bottles.profile.controller.dto

data class RegisterProfileRequestDto(
    val mbti: String,
    val keyword: List<String>,
    val interest: Interest,
    val job: String,
    val smoking: String,
    val alcohol: String,
    val religion: String,
    val region: Region
)