package com.nexters.bottles.user.facade.dto

import com.nexters.bottles.user.domain.QuestionAndAnswer

data class RegisterIntroductionRequestDto(
    val introduction: List<QuestionAndAnswer>
)
