package com.nexters.bottles.app.bottle.domain.enum

enum class LetterLastStatus {
    // 대화는 시작했으나 두 사람 모두 문답을 작성하지 않았을 때
    NO_ANSWER_FROM_BOTH,

    //상대방이 새로운 문답을 작성했을 때
    ANSWER_FROM_OTHER,

    // 상대방이 사진을 공유했을 때
    PHOTO_SHARED_BY_OTHER,

    // 상대방이 연락처를 공유했을 때
    CONTACT_SHARED_BY_OTHER,

    // 내가 문답을 작성했을 때 (상대방은 작성X)
    ANSWER_FROM_ME_ONLY,

    // 내가 사진을 공유했을 때 (상대방은 공유X)
    PHOTO_SHARED_BY_ME_ONLY,

    // 내가 연락처를 공유했을 때 (상대방은 공유X)
    CONTACT_SHARED_BY_ME_ONLY,

    // 대화가 중단됐을 때
    CONVERSATION_STOPPED
}
