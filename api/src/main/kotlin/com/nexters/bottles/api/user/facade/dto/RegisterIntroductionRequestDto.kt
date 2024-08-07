package com.nexters.bottles.api.user.facade.dto

import com.nexters.bottles.app.user.domain.QuestionAndAnswer

data class RegisterIntroductionRequestDto(
    val introduction: List<QuestionAndAnswer>
)
