package com.nexters.bottles.api.user.facade.dto

import com.nexters.bottles.api.user.domain.QuestionAndAnswer

data class RegisterIntroductionRequestDto(
    val introduction: List<QuestionAndAnswer>
)
