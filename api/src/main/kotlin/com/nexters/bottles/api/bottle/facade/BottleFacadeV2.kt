package com.nexters.bottles.api.bottle.facade

import com.nexters.bottles.api.bottle.event.dto.BottleMatchEventDto
import com.nexters.bottles.api.bottle.facade.dto.PingPongBottleDtoV2
import com.nexters.bottles.api.bottle.facade.dto.PingPongListResponseV2
import com.nexters.bottles.api.bottle.facade.dto.RandomBottleDto
import com.nexters.bottles.api.bottle.facade.dto.RandomBottleListResponse
import com.nexters.bottles.api.bottle.facade.dto.SentBottleDto
import com.nexters.bottles.api.bottle.facade.dto.SentBottleListResponse
import com.nexters.bottles.api.bottle.util.getLastActivatedAtInKorean
import com.nexters.bottles.api.user.component.event.dto.UserApplicationEventDto
import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.service.BottleCachingService
import com.nexters.bottles.app.bottle.service.BottleService
import com.nexters.bottles.app.bottle.service.LetterService
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.service.BlockContactListService
import com.nexters.bottles.app.user.service.UserReportService
import com.nexters.bottles.app.user.service.UserService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalTime

@Component
class BottleFacadeV2(
    private val bottleService: BottleService,
    private val userService: UserService,
    private val userReportService: UserReportService,
    private val blockContactListService: BlockContactListService,
    private val letterService: LetterService,
    private val bottleCachingService: BottleCachingService,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    fun getRandomBottles(userId: Long): RandomBottleListResponse {
        val user = userService.findByIdAndNotDeleted(userId)
        val blockUserIds = blockContactListService.findAllByUserId(userId).map { it.userId }.toSet() // 내가 차단한 유저
        val blockedMeUserIds = blockContactListService.findAllByPhoneNumber(
            user.phoneNumber ?: throw IllegalStateException("핸드폰 번호를 등록해주세요")
        ).map { it.userId }.toSet() // 나를 차단한 유저

        val matchingHour = 18
        bottleService.matchRandomBottle(user, matchingHour, blockUserIds, blockedMeUserIds)
            ?.also {
                applicationEventPublisher.publishEvent(
                    BottleMatchEventDto(
                        sourceUserId = it.sourceUser.id,
                        targetUserId = it.targetUser.id,
                    )
                )
            }

        val bottles = bottleService.getNewBottlesByBottleStatus(user, setOf(BottleStatus.RANDOM))
        val randomBottles = bottles.map { toRandomBottleDto(it, userId) }

        return RandomBottleListResponse(
            randomBottles = randomBottles,
            nextBottleLeftHours = getNextBottleLeftHours(LocalDateTime.now())
        ).also {
            publishUserApplicationEvent(user)
        }
    }

    private fun getNextBottleLeftHours(now: LocalDateTime): Int {
        return if (now.toLocalTime() > LocalTime.of(18, 0)) {
            18 + (LocalTime.MAX.hour - now.hour)
        } else {
            LocalTime.of(18, 0).hour - now.hour
        }
    }

    private fun toRandomBottleDto(bottle: Bottle, userId: Long): RandomBottleDto {
        return RandomBottleDto(
            id = bottle.id,
            userId = bottle.findOtherUserId(userId = userId),
            userName = bottle.sourceUser.getMaskedName(),
            age = bottle.sourceUser.getKoreanAge(),
            introduction = bottle.sourceUser.userProfile?.introduction,
            mbti = bottle.sourceUser.userProfile?.profileSelect?.mbti,
            keyword = bottle.sourceUser.userProfile?.profileSelect?.keyword,
            userImageUrl = bottle.sourceUser.userProfile?.blurredImageUrl,
            expiredAt = bottle.expiredAt,
            lastActivatedAt = getLastActivatedAtInKorean(
                basedAt = bottle.sourceUser.lastActivatedAt,
                now = LocalDateTime.now()
            )
        )
    }

    fun getSentBottles(userId: Long): SentBottleListResponse {
        val user = userService.findByIdAndNotDeleted(userId)
        val bottles = bottleService.getNewBottlesByBottleStatus(user, setOf(BottleStatus.SENT))

        if (bottles.isEmpty()) {
            return SentBottleListResponse(
                sentBottles = emptyList()
            ).also {
                publishUserApplicationEvent(user)
            }
        }

        val blockUserIds = blockContactListService.findAllByUserId(userId).map { it.userId }.toSet() // 내가 차단한 유저
        val blockedMeUserIds = blockContactListService.findAllByPhoneNumber(
            user.phoneNumber ?: throw IllegalStateException("핸드폰 번호를 등록해주세요")
        ).map { it.userId }.toSet() // 나를 차단한 유저
        val reportUserIds = userReportService.getReportRespondentList(userId)
            .map { it.respondentUserId }
            .toSet()

        val sentBottles = bottles.map { toSentBottleDto(it, userId) }
            .filter { it.userId !in reportUserIds }
            .filter { it.userId !in blockUserIds }
            .filter { it.userId !in blockedMeUserIds }

        return SentBottleListResponse(
            sentBottles = sentBottles
        ).also {
            publishUserApplicationEvent(user)
        }
    }

    private fun toSentBottleDto(bottle: Bottle, userId: Long): SentBottleDto {
        return SentBottleDto(
            id = bottle.id,
            userId = bottle.findOtherUserId(userId = userId),
            userName = bottle.sourceUser.getMaskedName(),
            age = bottle.sourceUser.getKoreanAge(),
            introduction = bottle.sourceUser.userProfile?.introduction,
            mbti = bottle.sourceUser.userProfile?.profileSelect?.mbti,
            keyword = bottle.sourceUser.userProfile?.profileSelect?.keyword,
            likeEmoji = bottle.likeMessage?.getLikeEmoji(),
            userImageUrl = bottle.sourceUser.userProfile?.blurredImageUrl,
            expiredAt = bottle.expiredAt,
            lastActivatedAt = getLastActivatedAtInKorean(
                basedAt = bottle.sourceUser.lastActivatedAt,
                now = LocalDateTime.now()
            )
        )
    }

    private fun publishUserApplicationEvent(user: User) {
        applicationEventPublisher.publishEvent(
            UserApplicationEventDto(
                userId = user.id,
                basedAt = LocalDateTime.now(),
            )
        )
    }

    fun getPingPongBottles(userId: Long): PingPongListResponseV2 {
        val user = userService.findByIdAndNotDeleted(userId)
        val pingPongBottles = bottleCachingService.getPingPongBottlesV2(userId)
        val reportUserIds = userReportService.getReportRespondentList(userId)
            .map { it.respondentUserId }
            .toSet()
        val blockUserIds = blockContactListService.findAllByUserId(userId).map { it.userId }.toSet() // 내가 차단한 유저
        val blockMeUserIds = blockContactListService.findAllByPhoneNumber(
            user.phoneNumber ?: throw IllegalStateException("핸드폰 번호를 등록해주세요")
        ).map { it.userId }.toSet() // 나를 차단한 유저

        return PingPongListResponseV2(
            pingPongBottles = pingPongBottles.filter { it.findOtherUserId(user.id) !in reportUserIds }
                .filter { it.findOtherUserId(user.id) !in blockUserIds }
                .filter { it.findOtherUserId(user.id) !in blockMeUserIds }
                .map { toPingPongBottleDto(it, user) }
        )
    }

    private fun toPingPongBottleDto(bottle: Bottle, user: User): PingPongBottleDtoV2 {
        val otherUser = bottle.findOtherUser(user)
        val otherUserLetter = letterService.findLetter(bottle, otherUser)
        val lastStatus = letterService.findLastStatus(bottle, user, otherUser)
        val lastUpdatedAt = letterService.findLastUpdated(bottle, user, otherUser)

        return PingPongBottleDtoV2(
            id = bottle.id,
            isRead = otherUserLetter.isReadByOtherUser,
            userName = otherUser.getMaskedName(),
            userId = otherUser.id,
            age = otherUser.getKoreanAge(),
            mbti = otherUser.userProfile?.profileSelect?.mbti,
            keyword = otherUser.userProfile?.profileSelect?.keyword,
            userImageUrl = otherUser.userProfile?.blurredImageUrl,
            lastActivatedAt = getLastActivatedAtInKorean(
                basedAt = lastUpdatedAt,
                now = LocalDateTime.now()
            ),
            lastStatus = lastStatus
        )
    }
}
