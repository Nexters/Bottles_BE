package com.nexters.bottles.bottle.facade

import com.nexters.bottles.bottle.facade.dto.BottleDto
import com.nexters.bottles.bottle.facade.dto.BottleListResponseDto
import com.nexters.bottles.bottle.service.BottleService
import com.nexters.bottles.pingpong.service.PingPongService
import org.springframework.stereotype.Component

@Component
class BottleFacade(
    private val bottleService: BottleService,
    private val pingPongService: PingPongService
) {

    fun getBottles(): BottleListResponseDto {
        val bottles = bottleService.getBottles()

        return BottleListResponseDto(
            bottles.map {
                BottleDto(
                    id = it.id!!,
                    userName = it.sourceUser.name,
                    age = 20, // TODO User에 age 추가된 후 수정
                    mbti = it.sourceUser.userProfile?.profileSelect?.mbti,
                    keyword = it.sourceUser.userProfile?.profileSelect?.keyword,
                    expiredAt = it.expiredAt
                )
            }
        )
    }

    fun acceptBottle(bottleId: Long) {
        val users = bottleService.validateBottleUsers(bottleId)
        pingPongService.startPingPong(users[0], users[1])
    }
}
