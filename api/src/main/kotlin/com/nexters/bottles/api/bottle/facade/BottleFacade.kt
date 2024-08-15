package com.nexters.bottles.api.bottle.facade

import com.nexters.bottles.api.bottle.event.dto.BottleApplicationEventDto
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
import com.nexters.bottles.api.user.component.event.dto.UserApplicationEventDto
import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.Letter
import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.bottle.service.BottleCachingService
import com.nexters.bottles.app.bottle.service.BottleService
import com.nexters.bottles.app.bottle.service.LetterService
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.domain.UserProfile
import com.nexters.bottles.app.user.service.UserReportService
import com.nexters.bottles.app.user.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalTime

@Component
class BottleFacade(
    private val bottleService: BottleService,
    private val letterService: LetterService,
    private val userService: UserService,
    private val userReportService: UserReportService,
    private val bottleCachingService: BottleCachingService,
    private val applicationEventPublisher: ApplicationEventPublisher,

    @Value("\${matching.isActive}")
    private val isActiveMatching: Boolean,
) {

    fun getNewBottles(userId: Long): BottleListResponse {
        val user = userService.findByIdAndNotDeleted(userId)
        if (isActiveMatching) {
            val matchingTime = LocalDateTime.now().with(LocalTime.of(18, 0))
            bottleService.matchRandomBottle(user, matchingTime)
                ?.also {
                    applicationEventPublisher.publishEvent(
                        BottleApplicationEventDto(
                            sourceUserId = it.sourceUser.id,
                            targetUserId = it.targetUser.id,
                            isRefused = false,
                        )
                    )
                }
        }
        val bottles = bottleService.getNewBottles(user)
        val groupByStatus = bottles.groupBy { it.bottleStatus }
        val blockedUserIds = userReportService.getReportRespondentList(userId)
            .map { it.respondentUserId }
            .toSet()

        val randomBottles = groupByStatus[BottleStatus.RANDOM]?.map { toBottleDto(it, userId) } ?: emptyList()
        val sentBottles = groupByStatus[BottleStatus.SENT]
            ?.map { toBottleDto(it, userId) }
            ?.filter { it.userId !in blockedUserIds }
            ?: emptyList()

        return BottleListResponse(
            randomBottles = randomBottles,
            sentBottles = sentBottles
        ).also {
            applicationEventPublisher.publishEvent(
                UserApplicationEventDto(
                    userId = user.id,
                    basedAt = LocalDateTime.now(),
                )
            )
        }
    }

    private fun toBottleDto(bottle: Bottle, userId: Long): BottleDto {
        return BottleDto(
            id = bottle.id,
            userId = bottle.findOtherUserId(userId = userId),
            userName = bottle.sourceUser.name,
            age = bottle.sourceUser.getKoreanAge(),
            mbti = bottle.sourceUser.userProfile?.profileSelect?.mbti,
            keyword = bottle.sourceUser.userProfile?.profileSelect?.keyword,
            userImageUrl = bottle.sourceUser.userProfile?.blurredImageUrl,
            expiredAt = bottle.expiredAt
        )
    }

    fun getBottle(userId: Long, bottleId: Long): BottleDetailResponse {
        val bottle = bottleService.getNotExpiredBottle(
            bottleId,
            setOf(PingPongStatus.NONE)
        )

        return BottleDetailResponse(
            id = bottle.id,
            userId = bottle.findOtherUserId(userId),
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
            .also {
                applicationEventPublisher.publishEvent(
                    BottleApplicationEventDto(
                        sourceUserId = userId,
                        targetUserId = it.findOtherUserId(userId),
                        isRefused = true
                    )
                )
            }
    }

    fun getPingPongBottles(userId: Long): PingPongListResponse {
        val user = userService.findByIdAndNotDeleted(userId)
        val pingPongBottles = bottleCachingService.getPingPongBottles(userId)
        val groupByStatus = pingPongBottles.groupBy { it.pingPongStatus }
        val blockedUserIds = userReportService.getReportRespondentList(userId)
            .map { it.respondentUserId }
            .toSet()

        val activeBottles = groupByStatus[PingPongStatus.ACTIVE]
            ?.map { toPingPongBottleDto(it, user) }
            ?.filter { it.userId !in blockedUserIds }
            ?: emptyList()
        val doneBottles =
            (groupByStatus[PingPongStatus.STOPPED].orEmpty() + groupByStatus[PingPongStatus.MATCHED].orEmpty())
                .map { toPingPongBottleDto(it, user) }
                .filter { it.userId !in blockedUserIds }
        return PingPongListResponse(activeBottles = activeBottles, doneBottles = doneBottles)
    }

    private fun toPingPongBottleDto(bottle: Bottle, user: User): PingPongBottleDto {
        val otherUser = bottle.findOtherUser(user)
        val otherUserLetter = letterService.findLetter(bottle, otherUser)

        return PingPongBottleDto(
            id = bottle.id,
            isRead = otherUserLetter.isReadByOtherUser,
            userName = otherUser.name,
            userId = otherUser.id,
            age = otherUser.getKoreanAge(),
            mbti = otherUser.userProfile?.profileSelect?.mbti,
            keyword = otherUser.userProfile?.profileSelect?.keyword,
            userImageUrl = otherUser.userProfile?.blurredImageUrl,
        )
    }

    fun registerLetter(userId: Long, bottleId: Long, registerLetterRequest: RegisterLetterRequest) {
        val pingPongBottle = bottleCachingService.getPingPongBottle(bottleId)
        val user = userService.findByIdAndNotDeleted(userId)

        letterService.registerLetter(
            pingPongBottle,
            user,
            registerLetterRequest.order,
            registerLetterRequest.answer
        )
    }

    fun readPingPongBottle(userId: Long, bottleId: Long) {
        val pingPongBottle = bottleCachingService.getPingPongBottle(bottleId)
        val me = userService.findByIdAndNotDeleted(userId)
        val otherUser = pingPongBottle.findOtherUser(me)
        letterService.markReadOtherUserLetter(pingPongBottle, otherUser)
    }

    @Caching(
        evict = [
            CacheEvict("pingPongBottleList", key = "#userId"),
            CacheEvict("pingPongBottle", key = "#bottleId")
        ]
    )
    fun stopBottle(userId: Long, bottleId: Long) {
        bottleService.stop(userId, bottleId)
    }

    fun getBottlePingPong(userId: Long, bottleId: Long): BottlePingPongResponse {
        val me = userService.findByIdAndNotDeleted(userId)
        val bottle = bottleCachingService.getPingPongBottle(bottleId)
        val otherUser = bottle.findOtherUser(me)
        val myLetter = letterService.findLetter(bottle, me)
        val otherLetter = letterService.findLetter(bottle, otherUser)

        return BottlePingPongResponse(
            isStopped = bottle.pingPongStatus == PingPongStatus.STOPPED,
            stopUserName = bottle.stoppedUser?.name,
            deleteAfterDays = getDeleteAfterDays(bottle),
            userProfile = PingPongUserProfile(
                userId = otherUser.id,
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
                shouldAnswer = myLetter.isShareContact == null,
                isFirstSelect = bottle.firstSelectUser == me
            )
        )
    }

    private fun getDeleteAfterDays(bottle: Bottle): Long? {
        if (!bottle.isStopped()) return null

        return bottle.calculateDeletedAfterDays()
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
            shouldAnswer = myLetter.isShareImage == null,
            myAnswer = myLetter.isShareImage,
            otherAnswer = otherLetter.isShareImage,
            isDone = (myLetter.isShareImage != null) && (otherLetter.isShareImage != null)
        )
    }

    @Caching(
        evict = [
            CacheEvict("pingPongBottleList", key = "#userId"),
            CacheEvict("pingPongBottle", key = "#bottleId")
        ]
    )
    fun selectShareImage(userId: Long, bottleId: Long, willShare: Boolean) {
        val user = userService.findByIdAndNotDeleted(userId)
        val pingPongBottle = bottleCachingService.getPingPongBottle(bottleId)

        letterService.shareImage(pingPongBottle, user, willShare)
    }

    @Caching(
        evict = [
            CacheEvict("pingPongBottleList", key = "#userId"),
            CacheEvict("pingPongBottle", key = "#bottleId")
        ]
    )
    fun selectMatch(userId: Long, bottleId: Long, willMatch: Boolean) {
        val user = userService.findByIdAndNotDeleted(userId)
        val pingPongBottle = bottleCachingService.getPingPongBottle(bottleId)

        val previousStatus = pingPongBottle.pingPongStatus
        letterService.shareContact(pingPongBottle, user, willMatch)
        val afterStatus = pingPongBottle.pingPongStatus

        // TODO: previousStatus는 match가 아니였는데 afterStatus가 match라면 푸시보내기
    }
}
