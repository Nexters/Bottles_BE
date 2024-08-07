package com.nexters.bottles.app.user.domain.enum

enum class Gender(
    val displayName: String,
) {
    MALE("남자"),
    FEMALE("여자"),
    ;

    companion object {

        fun fromString(value: String): Gender? {
            return when (value.uppercase()) {
                "MALE" -> MALE
                "FEMALE" -> FEMALE
                else -> null
            }
        }
    }
}
