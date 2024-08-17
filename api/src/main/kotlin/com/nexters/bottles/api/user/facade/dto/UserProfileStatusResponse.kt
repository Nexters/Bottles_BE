package com.nexters.bottles.api.user.facade.dto

class UserProfileStatusResponse(
    val userProfileStatus: UserProfileStatus
) {
}

enum class UserProfileStatus {
    EMPTY, // 아예 작성되지 않음
    ONLY_PROFILE_CREATED, // mbti 설정등 까지만 완료
    INTRODUCE_DONE, //자기소개까지 완료
    PHOTO_DONE, // 사진 등록까지 완료
    ;
}