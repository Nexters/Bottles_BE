package com.nexters.bottles.auth.facade.dto

import com.nexters.bottles.user.domain.enum.Gender
import java.time.LocalDate

data class SignUpRequest(
    val name: String,
    val birthYear: Int,
    val birthMonth: Int,
    val birthDay: Int,
    val gender: Gender,
    val phoneNumber: String,
    val authCode: String
) {

    fun convertBirthDateToLocalDate(): LocalDate {
        return LocalDate.of(birthYear, birthMonth, birthDay)
    }
}
