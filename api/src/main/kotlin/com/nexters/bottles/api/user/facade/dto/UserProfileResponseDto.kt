package com.nexters.bottles.api.user.facade.dto

import com.nexters.bottles.api.user.domain.QuestionAndAnswer
import com.nexters.bottles.api.user.domain.UserProfileSelect

data class UserProfileResponseDto(
    val userName: String,
    val age: Int,
    val imageUrl: String? = null,
    val introduction: List<QuestionAndAnswer>,
    val profileSelect: UserProfileSelect? = null,
) {
}
