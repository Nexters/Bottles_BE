package com.nexters.bottles.api.bottle.facade.dto

import com.nexters.bottles.api.user.domain.QuestionAndAnswer
import com.nexters.bottles.api.user.domain.UserProfileSelect

data class BottleDetailResponseDto(
    val id: Long,
    val userName: String,
    val age: Int,
    val introduction: List<QuestionAndAnswer>? = null,
    val profileSelect: UserProfileSelect? = null,
    val likeMessage: String? = null,
) {
}
