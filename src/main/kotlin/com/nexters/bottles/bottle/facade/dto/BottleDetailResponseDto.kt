package com.nexters.bottles.bottle.facade.dto

import com.nexters.bottles.user.domain.QuestionAndAnswer
import com.nexters.bottles.user.domain.UserProfileSelect

data class BottleDetailResponseDto(
    val id: Long,
    val userName: String,
    val age: Int,
    val introduction: List<QuestionAndAnswer>? = null,
    val profileSelect: UserProfileSelect? = null,
) {
}
