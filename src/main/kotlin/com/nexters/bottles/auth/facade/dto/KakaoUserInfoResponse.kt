package com.nexters.bottles.auth.facade.dto

import java.time.LocalDate
import java.time.YearMonth

data class KakaoUserInfoResponse(
    val id: Long,
    val connected_at: String,
    val synched_at: String,
    val kakao_account: KakaoAccount,
)

data class KakaoAccount(
    val name_needs_agreement: Boolean,
    val name: String,
    val has_phone_number: Boolean,
    val phone_number_needs_agreement: Boolean,
    val phone_number: String,
    val has_birthyear: Boolean,
    val birthyear_needs_agreement: Boolean,
    val birthyear: String,
    val has_birthday: Boolean,
    val birthday_needs_agreement: Boolean,
    val birthday: String,
    val birthday_type: String,
    val has_gender: Boolean,
    val gender_needs_agreement: Boolean,
    val gender: String,
    val birthDate: LocalDate? = null,
)
