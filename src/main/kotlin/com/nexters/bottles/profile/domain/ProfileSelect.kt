package com.nexters.bottles.profile.domain

import com.nexters.bottles.profile.controller.dto.Interest
import com.nexters.bottles.profile.controller.dto.Region

data class ProfileSelect(
    val mbti: String,
    val keyword: List<String> = arrayListOf(),
    val interest: Interest,
    val job: String,
    val smoking: String,
    val alcohol: String,
    val religion: String,
    val region: Region,
)