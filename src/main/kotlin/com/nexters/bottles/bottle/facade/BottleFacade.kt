package com.nexters.bottles.bottle.facade

import com.nexters.bottles.bottle.domain.Bottle
import com.nexters.bottles.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.bottle.facade.dto.BottleDetailResponseDto
import com.nexters.bottles.bottle.facade.dto.BottleDto
import com.nexters.bottles.bottle.facade.dto.BottleListResponseDto
import com.nexters.bottles.bottle.facade.dto.PingPongBottleDto
import com.nexters.bottles.bottle.facade.dto.PingPongListResponseDto
import com.nexters.bottles.bottle.facade.dto.RegisterLetterRequestDto
import com.nexters.bottles.bottle.domain.Bottle
import com.nexters.bottles.bottle.domain.Letter
import com.nexters.bottles.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.bottle.facade.dto.*
import com.nexters.bottles.bottle.service.BottleService
import com.nexters.bottles.bottle.service.LetterService
import com.nexters.bottles.user.domain.User
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class BottleFacade(
    private val bottleService: BottleService,
    private val letterService: LetterService
) {

    fun getNewBottles(): BottleListResponseDto {
        val bottles = bottleService.getNewBottles()

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
        val bottle = bottleService.getNotExpiredBottle(bottleId, setOf(PingPongStatus.NONE))

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
        val otherUserLetter = letterService.findLetter(bottle, otherUser)

        return PingPongBottleDto(
            id = bottle.id,
            isRead = otherUserLetter.isReadByOtherUser,
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

    fun readPingPongBottle(bottleId: Long) {
        val pingPongBottle = bottleService.getPingPongBottle(bottleId)
        val me = User() // TODO 회원 기능 구현 후 수정
        val otherUser = pingPongBottle.findOtherUser(me)
        letterService.readOtherUserLetter(pingPongBottle, otherUser)
    }

    fun stopBottle(bottleId: Long) {
        val pingPongBottle = bottleService.getPingPongBottle(bottleId)
        val me = User() // TODO 회원 기능 구현 후 수정

        pingPongBottle.stop(me)
    }

    fun getBottlePingPong(bottleId: Long): BottlePingpongResponseDto {
        val me = User(1L, LocalDate.of(2000, 1, 1), "보틀즈") // TODO: 회원 기능 갖추고 수정
        val bottle = bottleService.getPingPongBottle(
            bottleId,
            setOf(PingPongStatus.ACTIVE, PingPongStatus.STOPPED, PingPongStatus.MATCHED)
        )
        val otherUser = bottle.findOtherUser(me)
        val myLetter = letterService.findLetter(bottle, me)
        val otherLetter = letterService.findLetter(bottle, otherUser)

        return BottlePingpongResponseDto(
            isStopped = bottle.pingPongStatus == PingPongStatus.STOPPED,
            stopUserName = bottle.stoppedUser?.name,
            userProfile = PingPongUserProfile(
                userName = otherUser.name,
                age = otherUser.getKoreanAge(),
                profileSelect = otherUser.userProfile?.profileSelect
            ),
            introduction = otherUser.userProfile?.introduction,
            letters = getPingpongLetters(myLetter = myLetter, otherLetter = otherLetter),
            photo = getPhoto(myLetter = myLetter, otherLetter = otherLetter),
            matchResult = MatchResult(
                isMatched = bottle.pingPongStatus == PingPongStatus.MATCHED,
                contact = otherUser.kakaoId ?: throw IllegalArgumentException("고객센터에 문의 주세요")
            )
        )
    }

    private fun getPingpongLetters(myLetter: Letter?, otherLetter: Letter?): List<PingPongLetter> {
        if (myLetter == null || otherLetter == null) {
            // TODO: 편지가 언제 들어갈지 정하기. (ex. 보틀 매칭할 때 함께 넣어줄지)
            throw IllegalArgumentException("고객센터에 문의하세요")
        }

        return myLetter.letters.zip(otherLetter.letters).mapIndexed { index, (mySingleLetter, otherSingleLetter) ->

            PingPongLetter(
                order = index + 1,
                question = mySingleLetter.question,
                canshow = isFirstLetterOrPreviousBothAnswered(index, myLetter, otherLetter),
                myAnswer = mySingleLetter.answer,
                otherAnswer = otherSingleLetter.answer,
                shouldAnswer = mySingleLetter.answer == null,
                isDone = mySingleLetter.answer != null && otherSingleLetter.answer != null
            )
        }
    }

    private fun isFirstLetterOrPreviousBothAnswered(
        index: Int,
        myLetter: Letter,
        otherLetter: Letter
    ) = (index == 0) || (myLetter.letters[index - 1].answer != null && otherLetter.letters[index - 1].answer != null)

    private fun getPhoto(myLetter: Letter, otherLetter: Letter): Photo {

        return Photo(
            myImageUrl = myLetter.image,
            otherImageUrl = otherLetter.image,
            shouldAnswer = myLetter.image == null,
            changeFinished = (myLetter.image != null) && (otherLetter.image != null)
        )
    }
}
