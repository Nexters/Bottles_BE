package com.nexters.bottles.app.user.service.dto

import com.nexters.bottles.app.user.domain.enum.Gender
import java.time.LocalDate

data class SignUpProfileRequestV2(
    val name: String,
    val birthYear: Int,
    val birthMonth: Int,
    val birthDay: Int,
    val gender: Gender,
) {

    fun convertBirthDateToLocalDate(): LocalDate {
        return LocalDate.of(birthYear, birthMonth, birthDay)
    }
}
