package com.nexters.bottles.user.facade.dto

import com.nexters.bottles.user.domain.QuestionAndAnswer
import com.nexters.bottles.user.domain.UserProfileSelect

class UserProfileResponseDto(
    userName: String,
    age: Int,
    introduction: List<QuestionAndAnswer>? = null,
    profileSelect: UserProfileSelect? = null,
) {
}
