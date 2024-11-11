package com.nexters.bottles.app.bottle.repository.dto

data class UsersCanBeMatchedDto(
    val willMatchUserId: Long,
    val willMatchUserGender: String,
    val willMatchCity: String,
)

data class UserProfileSelectDto(
    val mbti: String,
    val keyword: List<String> = arrayListOf(),
    val interest: InterestDto,
    val job: String,
    val height: Int,
    val smoking: String,
    val alcohol: String,
    val religion: String,
    val region: RegionDto,
)

data class InterestDto(
    val culture: List<String> = arrayListOf(),
    val sports: List<String> = arrayListOf(),
    val entertainment: List<String> = arrayListOf(),
    val etc: List<String> = arrayListOf(),
)

data class RegionDto(
    val city: String,
    val state: String
)
