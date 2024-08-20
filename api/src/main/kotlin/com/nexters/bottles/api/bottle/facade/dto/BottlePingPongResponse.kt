package com.nexters.bottles.api.bottle.facade.dto

import com.nexters.bottles.app.user.domain.QuestionAndAnswer
import com.nexters.bottles.app.user.domain.UserProfileSelect

data class BottlePingPongResponse(
    val isStopped: Boolean = false,
    val stopUserName: String? = null,
    val deleteAfterDays: Long? = null,
    val userProfile: PingPongUserProfile,
    val introduction: List<QuestionAndAnswer>? = emptyList(),
    val letters: List<PingPongLetter> = emptyList(),
    val photo: Photo,
    val matchResult: MatchResult,
)

data class PingPongUserProfile(
    val userId: Long,
    val userName: String? = null,
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
    val photoStatus: PhotoStatus = PhotoStatus.NONE,
    val myImageUrl: String? = null,
    val otherImageUrl: String? = null,
    @Deprecated("iOS에서 status를 적용하면 삭제할 예정입니다")
    val shouldAnswer: Boolean,
    @Deprecated("iOS에서 status를 적용하면 삭제할 예정입니다")
    val myAnswer: Boolean? = null,
    @Deprecated("iOS에서 status를 적용하면 삭제할 예정입니다")
    val otherAnswer: Boolean? = null,
    @Deprecated("iOS에서 status를 적용하면 삭제할 예정입니다")
    val isDone: Boolean,
)

enum class PhotoStatus {
    NONE, // 아직 사진 교환 상태가 아닐 때
    MY_REJECT, // 내가 거절한 경우
    OTHER_REJECT, // 상대방이 거절한 경우
    REQUIRE_SELECT_OTHER_SELECT, // 상대방이 답하고 내가 답 안한 경우
    REQUIRE_SELECT_OTHER_NOT_SELECT, // 상대방이 답 안하고 내가 답 안한 경우
    WAITING_OTHER_ANSWER, // 내가 답하고 상대방이 답 안한 경우
    BOTH_AGREE, // 모두 동의한 경우
    ;
}

data class MatchResult(
    val matchStatus: MatchStatusType,
    val otherContact: String,
    val shouldAnswer: Boolean,
    val isFirstSelect: Boolean,
    val meetingPlace: String? = null,
    val meetingPlaceImageUrl: String? = null,
)

enum class MatchStatusType {
    NONE, // 아직 최종 선택 단계가 아닐 때
    REQUIRE_SELECT, // 최종 선택을 해야할 때
    WAITING_OTHER_ANSWER, // 상대의 답변을 기다려야 할 때
    MATCH_FAILED, // 매칭 실패 했을 때
    MATCH_SUCCEEDED, // 매칭 성공 했을 때
    ;
}
