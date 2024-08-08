package com.nexters.bottles.api.user.facade.dto

import com.nexters.bottles.app.user.domain.QuestionAndAnswer

data class RegisterIntroductionRequest(
    val introduction: List<QuestionAndAnswer>
)
