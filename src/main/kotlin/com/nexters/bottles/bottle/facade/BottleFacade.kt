package com.nexters.bottles.bottle.facade

import com.nexters.bottles.bottle.facade.dto.BottleDto
import com.nexters.bottles.bottle.facade.dto.BottleListResponseDto
import com.nexters.bottles.bottle.service.BottleService
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.ceil

@Component
class BottleFacade(
    private val bottleService: BottleService
) {

    fun getBottles(): BottleListResponseDto {
        val bottles = bottleService.getBottles()

        return BottleListResponseDto(
            bottles.map {
                BottleDto(
                    id = it.id!!,
                    userName = it.sourceUser.name!!,
                    age = 20, // TODO User에 age 추가된 후 수정
                    mbti = it.sourceUser.userProfile!!.profileSelect!!.mbti,
                    keyword = it.sourceUser.userProfile!!.profileSelect!!.keyword,
                    validTime = calculateValidTime(it.expiredAt)
                )
            }
        )
    }

    private fun calculateValidTime(expiredAt: LocalDateTime): Int {
        val now = LocalDateTime.now()
        val duration = Duration.between(now, expiredAt)
        val diffMinutes = duration.toMinutes()
        val diffHours = ceil(diffMinutes / 60.0)

        return diffHours.toInt()
    }
}
