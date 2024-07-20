package com.nexters.bottles.profile.domain

import com.nexters.bottles.profile.controller.dto.InterestDto
import com.nexters.bottles.profile.controller.dto.RegionDto

data class ProfileSelect(
    val mbti: String,
    val keyword: List<String> = arrayListOf(),
    val interest: InterestDto,
    val job: String,
    val smoking: String,
    val alcohol: String,
    val religion: String,
    val region: RegionDto,
)
