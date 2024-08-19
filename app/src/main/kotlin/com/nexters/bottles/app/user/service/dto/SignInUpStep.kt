package com.nexters.bottles.app.user.service.dto

enum class SignInUpStep {
    SIGN_IN,                             // 로그인
    SIGN_UP_SMS_FINISHED,                // 회원가입만 한 상태
    SIGN_UP_NAME_GENDER_AGE_FINISHED,    // 회원 가입후 이름,성별, 나이 입력한 상태
    ;
}
