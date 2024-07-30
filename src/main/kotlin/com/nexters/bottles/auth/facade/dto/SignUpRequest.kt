package com.nexters.bottles.auth.facade.dto

import com.nexters.bottles.user.domain.enum.Gender
import java.time.LocalDate

data class SignUpRequest(
    val name: String,
    val birthDate: String,
    val gender: Gender,
    val phoneNumber: String
) {
    fun convertToLocalDate(birthDate: String): LocalDate {
        val year = birthDate.substring(0, 2).toInt()
        val month = birthDate.substring(2, 4).toInt()
        val day = birthDate.substring(4, 6).toInt()
        return LocalDate.of(year, month, day)
    }
}
