package com.nexters.bottles.user.controller.dto

import com.nexters.bottles.user.domain.QuestionAndAnswer

data class RegisterIntroductionRequestDto(
    val introduction: List<QuestionAndAnswer>
)
