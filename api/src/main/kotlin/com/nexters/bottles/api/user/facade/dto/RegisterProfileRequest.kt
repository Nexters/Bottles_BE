package com.nexters.bottles.api.user.facade.dto

import com.nexters.bottles.app.user.domain.Interest
import com.nexters.bottles.app.user.domain.Region

data class RegisterProfileRequest(
    val mbti: String,
    val keyword: List<String>,
    val interest: InterestDto,
    var job: String,
    val height: Int,
    var smoking: String? = null,
    var alcohol: String? = null,
    val religion: String? = null,
    val region: RegionDto,
    val kakaoId: String,
)

data class InterestDto(
    val culture: List<String> = arrayListOf(),
    val sports: List<String> = arrayListOf(),
    val entertainment: List<String> = arrayListOf(),
    val etc: List<String> = arrayListOf(),
) {
    fun toDomain() = Interest(culture = culture, sports = sports, entertainment = entertainment, etc = etc)
}

data class RegionDto(
    val city: String,
    val state: String
) {
    fun toDomain() = Region(city = city, state = state)
}
