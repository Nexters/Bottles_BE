package com.nexters.bottles.user.facade.dto

import com.nexters.bottles.user.domain.QuestionAndAnswer
import com.nexters.bottles.user.domain.UserProfileSelect

data class UserProfileResponseDto(
    val userName: String,
    val age: Int,
    val imageUrl: String? = null,
    val introduction: List<QuestionAndAnswer>,
    val profileSelect: UserProfileSelect? = null,
) {
}
