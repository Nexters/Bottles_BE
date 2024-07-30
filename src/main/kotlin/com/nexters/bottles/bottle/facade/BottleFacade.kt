package com.nexters.bottles.bottle.facade

import com.nexters.bottles.bottle.domain.Bottle
import com.nexters.bottles.bottle.domain.Letter
import com.nexters.bottles.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.bottle.facade.dto.BottleDetailResponseDto
import com.nexters.bottles.bottle.facade.dto.BottleDto
import com.nexters.bottles.bottle.facade.dto.BottleListResponseDto
import com.nexters.bottles.bottle.facade.dto.BottlePingpongResponseDto
import com.nexters.bottles.bottle.facade.dto.MatchResult
import com.nexters.bottles.bottle.facade.dto.Photo
import com.nexters.bottles.bottle.facade.dto.PingPongBottleDto
import com.nexters.bottles.bottle.facade.dto.PingPongLetter
import com.nexters.bottles.bottle.facade.dto.PingPongListResponseDto
import com.nexters.bottles.bottle.facade.dto.PingPongUserProfile
import com.nexters.bottles.bottle.facade.dto.RegisterLetterRequestDto
import com.nexters.bottles.bottle.service.BottleService
import com.nexters.bottles.bottle.service.LetterService
import com.nexters.bottles.user.domain.User
import com.nexters.bottles.user.domain.UserProfile
import com.nexters.bottles.user.service.UserService
import org.springframework.stereotype.Component

@Component
class BottleFacade(
    private val bottleService: BottleService,
    private val letterService: LetterService,
    private val userService: UserService,
) {

    fun getNewBottles(userId: Long): BottleListResponseDto {
        val bottles = bottleService.getNewBottles(userId)

        return BottleListResponseDto(
            bottles.map {
                BottleDto(
                    id = it.id,
                    userName = it.sourceUser.name,
                    age = it.sourceUser.getKoreanAge(),
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
            userName = bottle.sourceUser.name,
            age = bottle.sourceUser.getKoreanAge(),
            introduction = bottle.sourceUser.userProfile?.introduction,
            profileSelect = bottle.sourceUser.userProfile?.profileSelect
        )
    }

    fun acceptBottle(userId: Long, bottleId: Long) {
        bottleService.acceptBottle(userId, bottleId)
    }

    fun refuseBottle(userId: Long, bottleId: Long) {
        bottleService.refuseBottle(userId, bottleId)
    }

    fun getPingPongBottles(userId: Long): PingPongListResponseDto {
        val pingPongBottles = bottleService.getPingPongBottles(userId)
        val user = userService.findById(userId)

        val groupByStatus = pingPongBottles.groupBy { it.pingPongStatus }
        val activeBottles =
            (groupByStatus[PingPongStatus.ACTIVE].orEmpty() + groupByStatus[PingPongStatus.STOPPED].orEmpty())
                .map { toPingPongBottleDto(it, user) }
        val doneBottles = groupByStatus[PingPongStatus.MATCHED]?.map { toPingPongBottleDto(it, user) } ?: emptyList()
        return PingPongListResponseDto(activeBottles = activeBottles, doneBottles = doneBottles)
    }

    private fun toPingPongBottleDto(bottle: Bottle, user: User): PingPongBottleDto {
        val otherUser = bottle.findOtherUser(user)
        val otherUserLetter = letterService.findLetter(bottle, otherUser)

        return PingPongBottleDto(
            id = bottle.id,
            isRead = otherUserLetter.isReadByOtherUser,
            userName = otherUser.name,
            age = otherUser.getKoreanAge(),
            mbti = otherUser.userProfile?.profileSelect?.mbti,
            keyword = otherUser.userProfile?.profileSelect?.keyword
        )
    }

    fun registerLetter(userId: Long, bottleId: Long, registerLetterRequestDto: RegisterLetterRequestDto) {
        val pingPongBottle = bottleService.getPingPongBottle(bottleId)
        val user = userService.findById(userId)

        letterService.registerLetter(
            pingPongBottle,
            user,
            registerLetterRequestDto.order,
            registerLetterRequestDto.answer
        )
    }

    fun readPingPongBottle(userId: Long, bottleId: Long) {
        val pingPongBottle = bottleService.getPingPongBottle(bottleId)
        val me = userService.findById(userId)
        val otherUser = pingPongBottle.findOtherUser(me)
        letterService.readOtherUserLetter(pingPongBottle, otherUser)
    }

    fun stopBottle(userId: Long, bottleId: Long) {
        val pingPongBottle = bottleService.getPingPongBottle(bottleId)
        val me = userService.findById(userId)

        pingPongBottle.stop(me)
    }

    fun getBottlePingPong(userId: Long, bottleId: Long): BottlePingpongResponseDto {
        val me = userService.findById(userId)
        val bottle = bottleService.getPingPongBottle(bottleId)
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
            letters = getPingPongLetters(myLetter = myLetter, otherLetter = otherLetter),
            photo = getPhoto(
                myLetter = myLetter,
                otherLetter = otherLetter,
                myProfile = me.userProfile!!,
                otherProfile = otherUser.userProfile!!
            ),
            matchResult = MatchResult(
                isMatched = bottle.pingPongStatus == PingPongStatus.MATCHED,
                contact = otherUser.kakaoId ?: throw IllegalArgumentException("고객센터에 문의 주세요")
            )
        )
    }

    private fun getPingPongLetters(myLetter: Letter?, otherLetter: Letter?): List<PingPongLetter> {
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

    private fun getPhoto(
        myLetter: Letter,
        otherLetter: Letter,
        myProfile: UserProfile,
        otherProfile: UserProfile
    ): Photo {

        return Photo(
            myImageUrl = myProfile.imageUrl,
            otherImageUrl = otherProfile.imageUrl,
            shouldAnswer = myLetter.isShowImage == null,
            changeFinished = (myLetter.isShowImage != null) && (otherLetter.isShowImage != null)
        )
    }

    fun selectShareImage(userId: Long, bottleId: Long, willShare: Boolean) {
        val user = userService.findById(userId)
        val pingPongBottle = bottleService.getPingPongBottle(bottleId)

        val letter = letterService.findLetter(pingPongBottle, user)
        letter.shareImage(willShare)
        letter.markUnread()

        if (!willShare) {
            pingPongBottle.stop(user)
        }
    }

    fun selectMatch(userId: Long, bottleId: Long, willMatch: Boolean) {
        val user = userService.findById(userId)

        val previousStatus = bottleService.getPingPongBottle(bottleId).pingPongStatus
        val pingPongBottle = bottleService.selectMatch(
            userId = user.id,
            bottleId = bottleId,
            willMatch = willMatch,
        )
        val afterStatus = pingPongBottle.pingPongStatus

        // TODO: previousStatus는 match가 아니였는데 afterStatus가 match라면 푸시보내기
    }
}
