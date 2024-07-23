package com.nexters.bottles.bottle.facade

import com.nexters.bottles.bottle.facade.dto.BottleDetailResponseDto
import com.nexters.bottles.bottle.facade.dto.BottleDto
import com.nexters.bottles.bottle.facade.dto.BottleListResponseDto
import com.nexters.bottles.bottle.service.BottleService
import org.springframework.stereotype.Component

@Component
class BottleFacade(
    private val bottleService: BottleService
) {

    fun getBottles(): BottleListResponseDto {
        val bottles = bottleService.getBottles()

        return BottleListResponseDto(
            bottles.map {
                BottleDto(
                    id = it.id,
                    userName = "it.sourceUser.name",
                    age = 20, // TODO User에 age 추가된 후 수정
                    mbti = it.sourceUser.userProfile?.profileSelect?.mbti,
                    keyword = it.sourceUser.userProfile?.profileSelect?.keyword,
                    expiredAt = it.expiredAt
                )
            }
        )
    }

    fun getBottle(bottleId: Long): BottleDetailResponseDto {
        val bottle = bottleService.getBottle(bottleId)

        return BottleDetailResponseDto(
            id = bottle.id,
            userName = "bottle.sourceUser.name", // TODO User 변경된 후 수정
            age = 20,
            introduction = bottle.sourceUser.userProfile?.introduction,
            profileSelect = bottle.sourceUser.userProfile?.profileSelect
        )
    }

    fun acceptBottle(bottleId: Long) {
        bottleService.acceptBottle(bottleId)
    }
}
