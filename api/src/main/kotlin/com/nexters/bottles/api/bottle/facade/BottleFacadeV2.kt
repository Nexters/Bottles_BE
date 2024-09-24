package com.nexters.bottles.api.bottle.facade

import com.nexters.bottles.api.bottle.event.dto.BottleMatchEventDto
import com.nexters.bottles.api.bottle.facade.dto.RandomBottleDto
import com.nexters.bottles.api.bottle.facade.dto.RandomBottleListResponse
import com.nexters.bottles.api.bottle.facade.dto.SentBottleDto
import com.nexters.bottles.api.bottle.facade.dto.SentBottleListResponse
import com.nexters.bottles.api.bottle.util.getLastActivatedAtInKorean
import com.nexters.bottles.api.user.component.event.dto.UserApplicationEventDto
import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.service.BottleService
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

    private fun publishUserApplicationEvent(user: User) {
        applicationEventPublisher.publishEvent(
            UserApplicationEventDto(
                userId = user.id,
                basedAt = LocalDateTime.now(),
            )
        )
    }
}
