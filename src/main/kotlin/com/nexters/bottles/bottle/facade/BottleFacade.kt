package com.nexters.bottles.bottle.facade

import com.nexters.bottles.bottle.domain.Bottle
import com.nexters.bottles.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.bottle.facade.dto.BottleDetailResponseDto
import com.nexters.bottles.bottle.facade.dto.BottleDto
import com.nexters.bottles.bottle.facade.dto.BottleListResponseDto
import com.nexters.bottles.bottle.facade.dto.PingPongBottleDto
import com.nexters.bottles.bottle.facade.dto.PingPongListResponseDto
import com.nexters.bottles.bottle.facade.dto.RegisterLetterRequestDto
import com.nexters.bottles.bottle.service.BottleService
import com.nexters.bottles.bottle.service.LetterService
import com.nexters.bottles.user.domain.User
import org.springframework.stereotype.Component

@Component
class BottleFacade(
    private val bottleService: BottleService,
    private val letterService: LetterService
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

    fun refuseBottle(bottleId: Long) {
        bottleService.refuseBottle(bottleId)
    }

    fun getPingPongBottles(): PingPongListResponseDto {
        val pingPongBottles = bottleService.getPingPongBottles()
        val user = User() // TODO 회원 기능 구현 후 수정

        val groupByStatus = pingPongBottles.groupBy { it.pingPongStatus }
        val activeBottles = groupByStatus[PingPongStatus.ACTIVE]?.map { toPingPongBottleDto(it, user) } ?: emptyList()
        val doneBottles = groupByStatus[PingPongStatus.DONE]?.map { toPingPongBottleDto(it, user) } ?: emptyList()
        return PingPongListResponseDto(activeBottles = activeBottles, doneBottles = doneBottles)
    }

    private fun toPingPongBottleDto(bottle: Bottle, user: User): PingPongBottleDto {
        val otherUser = bottle.findOtherUser(user)
        val letter = letterService.findLetter(bottle, otherUser)

        return PingPongBottleDto(
            id = bottle.id,
            isRead = letter.isRead,
            userName = "otherUser.name", // TODO User 변경된 후 수정
            age = 20,
            mbti = otherUser.userProfile?.profileSelect?.mbti,
            keyword = otherUser.userProfile?.profileSelect?.keyword
        )
    }

    fun registerLetter(bottleId: Long, registerLetterRequestDto: RegisterLetterRequestDto) {
        val pingPongBottle = bottleService.getPingPongBottle(bottleId)
        val user = User() // TODO 회원 기능 구현 후 수정

        letterService.registerLetter(
            pingPongBottle,
            user,
            registerLetterRequestDto.order,
            registerLetterRequestDto.answer
        )
    }
}
