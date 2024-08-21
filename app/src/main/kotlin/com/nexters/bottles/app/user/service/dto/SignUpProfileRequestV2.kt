package com.nexters.bottles.app.user.service.dto

import com.nexters.bottles.app.user.domain.enum.Gender
import java.time.LocalDate

data class SignUpProfileRequestV2(
    val name: String,
    val birthYear: Int? = LocalDate.now().year,
    val birthMonth: Int? = LocalDate.now().monthValue,
    val birthDay: Int? = LocalDate.now().dayOfMonth,
    val gender: Gender? = Gender.FEMALE,
) {

    fun convertBirthDateToLocalDate(): LocalDate {
        return LocalDate.of(birthYear!!, birthMonth!!, birthDay!!)
    }
}
