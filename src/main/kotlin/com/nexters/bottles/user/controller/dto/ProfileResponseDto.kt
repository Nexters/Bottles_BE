package com.nexters.bottles.user.controller.dto

import com.nexters.bottles.user.domain.QuestionAndAnswer
import com.nexters.bottles.user.domain.UserProfileSelect

data class ProfileResponseDto(
    val profileSelect: UserProfileSelect?,
    val introduction: List<QuestionAndAnswer>,
) {

    companion object {

        private val emptyResponse = ProfileResponseDto(
            profileSelect = null,
            introduction = emptyList()
        )

        fun ofEmpty(): ProfileResponseDto {
            return emptyResponse
        }
    }
}
