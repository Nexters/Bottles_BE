package com.nexters.bottles.api.bottle.facade.dto

import com.nexters.bottles.app.user.domain.QuestionAndAnswer
import com.nexters.bottles.app.user.domain.UserProfileSelect

data class BottlePingPongResponse(
    val isStopped: Boolean = false,
    val stopUserName: String? = null,
    val userProfile: PingPongUserProfile,
    val introduction: List<QuestionAndAnswer>? = emptyList(),
    val letters: List<PingPongLetter> = emptyList(),
    val photo: Photo,
    val matchResult: MatchResult,
)

data class PingPongUserProfile(
    val userName: String,
    val age: Int,
    val profileSelect: UserProfileSelect? = null,
    val userImageUrl: String? = null,
)

data class PingPongLetter(
    val order: Int,
    val question: String,
    val canshow: Boolean,
    val myAnswer: String? = null,
    val otherAnswer: String? = null,
    val shouldAnswer: Boolean,
    val isDone: Boolean,
)

data class Photo(
    val myImageUrl: String? = null,
    val otherImageUrl: String? = null,
    val shouldAnswer: Boolean,
    val changeFinished: Boolean,
)

data class MatchResult(
    val isMatched: Boolean,
    val contact: String,
)
