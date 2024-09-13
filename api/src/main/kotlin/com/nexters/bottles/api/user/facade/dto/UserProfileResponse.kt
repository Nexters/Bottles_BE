package com.nexters.bottles.api.user.facade.dto

import com.nexters.bottles.app.user.domain.QuestionAndAnswer
import com.nexters.bottles.app.user.domain.UserProfileSelect

data class UserProfileResponse(
    val userName: String?,
    val age: Int,
    val imageUrl: String? = null,
    val introduction: List<QuestionAndAnswer>,
    val profileSelect: UserProfileSelect? = null,
    val isMatchActivated: Boolean? = true
) {
}
