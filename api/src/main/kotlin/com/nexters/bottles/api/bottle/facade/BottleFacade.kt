package com.nexters.bottles.api.bottle.facade

import com.nexters.bottles.api.bottle.facade.dto.AcceptBottleRequest
import com.nexters.bottles.api.bottle.facade.dto.BottleDetailResponse
import com.nexters.bottles.api.bottle.facade.dto.BottleDto
import com.nexters.bottles.api.bottle.facade.dto.BottleListResponse
import com.nexters.bottles.api.bottle.facade.dto.BottlePingPongResponse
import com.nexters.bottles.api.bottle.facade.dto.MatchResult
import com.nexters.bottles.api.bottle.facade.dto.Photo
import com.nexters.bottles.api.bottle.facade.dto.PingPongBottleDto
import com.nexters.bottles.api.bottle.facade.dto.PingPongLetter
import com.nexters.bottles.api.bottle.facade.dto.PingPongListResponse
import com.nexters.bottles.api.bottle.facade.dto.PingPongUserProfile
import com.nexters.bottles.api.bottle.facade.dto.RegisterLetterRequest
import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.Letter
import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.bottle.service.BottleService
import com.nexters.bottles.app.bottle.service.LetterService
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.domain.UserProfile
import com.nexters.bottles.app.user.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalTime

@Component
class BottleFacade(
    private val bottleService: BottleService,
    private val letterService: LetterService,
    private val userService: UserService,

    @Value("\${matching.isActive}")
    private val isActiveMatching: Boolean,
) {

    fun getNewBottles(userId: Long): BottleListResponse {
        val user = userService.findByIdAndNotDeleted(userId)
        if (isActiveMatching) {
            val matchingTime = LocalDateTime.now().with(LocalTime.of(18, 0))
            bottleService.matchRandomBottle(user, matchingTime)
        }
        val bottles = bottleService.getNewBottles(user)
        val groupByStatus = bottles.groupBy { it.bottleStatus }

        val randomBottles = groupByStatus[BottleStatus.RANDOM]?.map { toBottleDto(it) } ?: emptyList()
        val sentBottles = groupByStatus[BottleStatus.SENT]?.map { toBottleDto(it) } ?: emptyList()

        return BottleListResponse(
            randomBottles = randomBottles,
            sentBottles = sentBottles
        )
    }

    private fun toBottleDto(bottle: Bottle): BottleDto {
        return BottleDto(
            id = bottle.id,
            userName = bottle.sourceUser.name,
            age = bottle.sourceUser.getKoreanAge(),
            mbti = bottle.sourceUser.userProfile?.profileSelect?.mbti,
            keyword = bottle.sourceUser.userProfile?.profileSelect?.keyword,
            userImageUrl = bottle.sourceUser.userProfile?.blurredImageUrl,
            expiredAt = bottle.expiredAt
        )
    }

    fun getBottle(bottleId: Long): BottleDetailResponse {
        val bottle = bottleService.getNotExpiredBottle(
            bottleId,
            setOf(PingPongStatus.NONE)
        )

        return BottleDetailResponse(
            id = bottle.id,
            userName = bottle.sourceUser.name,
            age = bottle.sourceUser.getKoreanAge(),
            introduction = bottle.sourceUser.userProfile?.introduction,
            profileSelect = bottle.sourceUser.userProfile?.profileSelect,
            likeMessage = bottle.likeMessage,
            userImageUrl = bottle.sourceUser.userProfile?.blurredImageUrl
        )
    }

    fun acceptBottle(userId: Long, bottleId: Long, acceptBottleRequest: AcceptBottleRequest) {
        bottleService.acceptBottle(userId, bottleId, acceptBottleRequest.likeMessage)
    }

    fun refuseBottle(userId: Long, bottleId: Long) {
        bottleService.refuseBottle(userId, bottleId)
    }

    fun getPingPongBottles(userId: Long): PingPongListResponse {
        val pingPongBottles = bottleService.getPingPongBottles(userId)
        val user = userService.findByIdAndNotDeleted(userId)

        val groupByStatus = pingPongBottles.groupBy { it.pingPongStatus }
        val activeBottles =
            (groupByStatus[PingPongStatus.ACTIVE].orEmpty() + groupByStatus[PingPongStatus.STOPPED].orEmpty())
                .map { toPingPongBottleDto(it, user) }
        val doneBottles = groupByStatus[PingPongStatus.MATCHED]?.map {
            toPingPongBottleDto(
                it,
                user
            )
        } ?: emptyList()
        return PingPongListResponse(activeBottles = activeBottles, doneBottles = doneBottles)
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
            keyword = otherUser.userProfile?.profileSelect?.keyword,
            userImageUrl = otherUser.userProfile?.blurredImageUrl,
        )
    }

    fun registerLetter(userId: Long, bottleId: Long, registerLetterRequest: RegisterLetterRequest) {
        val pingPongBottle = bottleService.getPingPongBottle(bottleId)
        val user = userService.findByIdAndNotDeleted(userId)

        letterService.registerLetter(
            pingPongBottle,
            user,
            registerLetterRequest.order,
            registerLetterRequest.answer
        )
    }

    fun readPingPongBottle(userId: Long, bottleId: Long) {
        val pingPongBottle = bottleService.getPingPongBottle(bottleId)
        val me = userService.findByIdAndNotDeleted(userId)
        val otherUser = pingPongBottle.findOtherUser(me)
        letterService.readOtherUserLetter(pingPongBottle, otherUser)
    }

    fun stopBottle(userId: Long, bottleId: Long) {
        bottleService.stop(userId, bottleId)
    }

    fun getBottlePingPong(userId: Long, bottleId: Long): BottlePingPongResponse {
        val me = userService.findByIdAndNotDeleted(userId)
        val bottle = bottleService.getPingPongBottle(bottleId)
        val otherUser = bottle.findOtherUser(me)
        val myLetter = letterService.findLetter(bottle, me)
        val otherLetter = letterService.findLetter(bottle, otherUser)

        return BottlePingPongResponse(
            isStopped = bottle.pingPongStatus == PingPongStatus.STOPPED,
            stopUserName = bottle.stoppedUser?.name,
            userProfile = PingPongUserProfile(
                userName = otherUser.name,
                age = otherUser.getKoreanAge(),
                profileSelect = otherUser.userProfile?.profileSelect,
                userImageUrl = otherUser.userProfile?.blurredImageUrl
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
                otherContact = otherUser.kakaoId ?: throw IllegalArgumentException("고객센터에 문의 주세요"),
                shouldAnswer = myLetter.isShowContact == null,
                isFirstSelect = bottle.firstSelectUser == me
            )
        )
    }

    private fun getPingPongLetters(myLetter: Letter, otherLetter: Letter): List<PingPongLetter> {
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
            myAnswer = myLetter.isShowImage,
            otherAnswer = otherLetter.isShowImage,
            isDone = (myLetter.isShowImage != null) && (otherLetter.isShowImage != null)
        )
    }

    fun selectShareImage(userId: Long, bottleId: Long, willShare: Boolean) {
        val user = userService.findByIdAndNotDeleted(userId)
        val pingPongBottle = bottleService.getPingPongBottle(bottleId)

        letterService.shareImage(pingPongBottle, user, willShare)
    }

    fun selectMatch(userId: Long, bottleId: Long, willMatch: Boolean) {
        val user = userService.findByIdAndNotDeleted(userId)
        val pingPongBottle = bottleService.getPingPongBottle(bottleId)

        val previousStatus = pingPongBottle.pingPongStatus
        letterService.shareContact(pingPongBottle, user, willMatch)
        val afterStatus = pingPongBottle.pingPongStatus

        // TODO: previousStatus는 match가 아니였는데 afterStatus가 match라면 푸시보내기
    }
}
