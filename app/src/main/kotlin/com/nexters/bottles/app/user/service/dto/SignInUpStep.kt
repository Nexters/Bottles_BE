package com.nexters.bottles.app.user.service.dto

enum class SignInUpStep {
    SIGN_UP_APPLE_LOGIN_FINISHED,        // 애플 로그인만 한 상태
    SIGN_UP_NAME_GENDER_AGE_FINISHED,    // 애플 로그인 후 이름, 성별, 나이 입력한 상태
    ;
}
