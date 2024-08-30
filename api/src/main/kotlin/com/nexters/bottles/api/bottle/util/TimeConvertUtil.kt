package com.nexters.bottles.api.bottle.util

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun getLastActivatedAtInKorean(basedAt: LocalDateTime, now: LocalDateTime): String {
    val minutesBetween = ChronoUnit.MINUTES.between(basedAt, now)

    return when {
        minutesBetween >= 1440 -> "${minutesBetween / 1440}일 전"  // 1440분은 24시간
        minutesBetween >= 60 -> "${minutesBetween / 60}시간 전"
        else -> "${minutesBetween}분 전"
    }
}
