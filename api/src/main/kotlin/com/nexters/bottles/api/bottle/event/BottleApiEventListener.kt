package com.nexters.bottles.api.bottle.event

import com.nexters.bottles.api.bottle.event.dto.BottleAcceptEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleMatchEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleReadEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleRefuseEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleRegisterLetterEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleShareContactEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleShareImageEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleStopEventDto
import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.enum.TabType
import com.nexters.bottles.app.bottle.service.BottleHistoryService
import com.nexters.bottles.app.bottle.service.BottleReadHistoryService
import com.nexters.bottles.app.bottle.service.BottleService
import com.nexters.bottles.app.bottle.service.TabEventService
import com.nexters.bottles.app.bottle.service.dto.TabEventDto
import com.nexters.bottles.app.notification.component.FcmClient
import com.nexters.bottles.app.notification.component.dto.FcmNotification
import com.nexters.bottles.app.notification.service.FcmTokenService
import com.nexters.bottles.app.user.domain.enum.AlimyType
import com.nexters.bottles.app.user.service.UserAlimyService
import com.nexters.bottles.app.user.service.UserService
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class BottleApiEventListener(
    private val bottleService: BottleService,
    private val bottleHistoryService: BottleHistoryService,
    private val bottleReadHistoryService: BottleReadHistoryService,
    private val fcmTokenService: FcmTokenService,
    private val fcmClient: FcmClient,
    private val userService: UserService,
    private val userAlimyService: UserAlimyService,
    private val tabEventService: TabEventService
) {

    private val log = KotlinLogging.logger { }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleRefuseEventDto) {
        bottleHistoryService.saveRefuseHistory(event.sourceUserId, event.targetUserId)
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleMatchEventDto) {
        bottleHistoryService.saveMatchingHistory(sourceUserId = event.sourceUserId, targetUserId = event.targetUserId)
        bottleReadHistoryService.saveBottleReadHistory(userId = event.sourceUserId, bottleId = event.bottleId)
        bottleReadHistoryService.saveBottleReadHistory(userId = event.targetUserId, bottleId = event.bottleId)
        tabEventService.sendEventByTabType(
            event.targetUserId,
            TabEventDto(tabType = TabType.SANDBEACH, isNewBadgeVisible = true)
        )
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleAcceptEventDto) {
        val bottle = bottleService.findBottleById(event.bottleId)
        when {
            bottle.isSentLikeMessageAndNotStart() -> {
                bottleHistoryService.saveMatchingHistory(bottle.sourceUser.id, bottle.targetUser.id)
                tabEventService.sendEventByTabType(
                    bottle.targetUser.id,
                    TabEventDto(tabType = TabType.LIKE, isNewBadgeVisible = true)
                )

                if (!userAlimyService.isTurnedOn(bottle.targetUser.id, AlimyType.RECEIVE_LIKE)) {
                    log.info { "userId: ${bottle.targetUser.id} alimyType: ${AlimyType.RECEIVE_LIKE} 켜져 있지 않아 발송하지 않음" }
                    return
                }

                val fcmNotification = FcmNotification(
                    title = "누군가 ${bottle.targetUser.name}님에게 편지를 보냈어요! 💘",
                    body = "${bottle.targetUser.name}님에게 호감을 표현한 사람이 있어요.\n도착한 보틀을 확인해주세요!"
                )

                fcmTokenService.findAllByUserIdAndTokenNotBlank(bottle.targetUser.id).forEach {
                    fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
                    log.info { "[BottleAcceptEventDto] 호감 보냄 bottleId: ${bottle.id} targetUserId: ${bottle.targetUser.id} sourceUserId: ${bottle.sourceUser.id} sourceUserToken: ${it.token}" }
                }

            }

            bottle.isActive() -> {
                tabEventService.sendEventByTabType(
                    bottle.targetUser.id,
                    TabEventDto(tabType = TabType.PINGPONG, isNewBadgeVisible = true)
                )
                tabEventService.sendEventByTabType(
                    bottle.sourceUser.id,
                    TabEventDto(tabType = TabType.PINGPONG, isNewBadgeVisible = true)
                )

                fcmTokenService.findAllByUserIdsAndTokenNotBlank(listOf(bottle.sourceUser.id, bottle.targetUser.id))
                    .forEach {
                        if (!userAlimyService.isTurnedOn(it.userId, AlimyType.PINGPONG)) {
                            log.info { "userId: ${it.userId} alimyType: ${AlimyType.PINGPONG} 켜져 있지 않아 발송하지 않음" }
                            return@forEach
                        }

                        val fcmNotification = FcmNotification(
                            title = "${findOtherUserName(it.userId, bottle)}님과의 문답이 시작됐어요! 💌",
                            body = "어떤 질문이 기다리고 있을까요?\n지금부터 서로를 더 깊게 알아보세요!"
                        )
                        fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
                        log.info { "[BottleAcceptEventDto] 문답 시작 bottleId: ${bottle.id} userId: ${it.userId} token: ${it.token}" }
                    }
            }
        }
    }

    private fun findOtherUserName(userId: Long, bottle: Bottle): String {
        return if (userId == bottle.sourceUser.id) bottle.targetUser.getMaskedName() else bottle.sourceUser.getMaskedName()
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleStopEventDto) {
        val bottle = bottleService.findBottleById(event.bottleId)
        val otherUser = bottle.findOtherUser(bottle.stoppedUser!!)

        tabEventService.sendEventByTabType(
            otherUser.id,
            TabEventDto(tabType = TabType.PINGPONG, isNewBadgeVisible = true)
        )

        fcmTokenService.findAllByUserIdAndTokenNotBlank(otherUser.id).forEach {
            if (!userAlimyService.isTurnedOn(otherUser.id, AlimyType.PINGPONG)) {
                log.info { "userId: ${otherUser.id} alimyType: ${AlimyType.PINGPONG} 켜져 있지 않아 발송하지 않음" }
                return
            }

            val fcmNotification = FcmNotification(
                title = "아쉬워요! 다른 보틀을 열어볼까요? 😢",
                body = "${bottle.stoppedUser!!.getMaskedName()}님이 대화를 중단했어요.\n대화는 3일 뒤에 삭제돼요."
            )
            fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
            log.info { "[BottleStopEventDto] 대화 중지 token: ${it.token}" }
        }
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleRegisterLetterEventDto) {
        val bottle = bottleService.findBottleById(event.bottleId)
        val user = userService.findByIdAndNotDeleted(event.userId)
        val otherUser = bottle.findOtherUser(user)

        tabEventService.sendEventByTabType(
            otherUser.id,
            TabEventDto(tabType = TabType.PINGPONG, isNewBadgeVisible = true)
        )

        fcmTokenService.findAllByUserIdAndTokenNotBlank(otherUser.id).forEach {
            if (!userAlimyService.isTurnedOn(otherUser.id, AlimyType.PINGPONG)) {
                log.info { "userId: ${otherUser.id} alimyType: ${AlimyType.PINGPONG} 켜져 있지 않아 발송하지 않음" }
                return
            }

            val fcmNotification = FcmNotification(
                title = "${user.getMaskedName()}님이 답변을 완료했어요 👀",
                body = "두근두근, ${user.getMaskedName()}님은 어떻게 생각할까요?\n지금 바로 확인해 보세요!"
            )
            fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
            log.info { "[BottleRegisterLetterEventDto] bottleId: ${bottle.id} otherUserId: ${otherUser.id} token: ${it.token}" }
        }
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleReadEventDto) {
        val isAllRead = bottleService.isAllReadPingPongBottles(event.userId)

        tabEventService.sendEventByTabType(
            event.userId,
            TabEventDto(tabType = TabType.PINGPONG, isNewBadgeVisible = !isAllRead)
        )
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleShareImageEventDto) {
        val bottle = bottleService.findBottleById(event.bottleId)
        val user = userService.findByIdAndNotDeleted(event.userId)
        val otherUser = bottle.findOtherUser(user)

        tabEventService.sendEventByTabType(
            otherUser.id,
            TabEventDto(tabType = TabType.PINGPONG, isNewBadgeVisible = true)
        )

        fcmTokenService.findAllByUserIdAndTokenNotBlank(otherUser.id).forEach {
            if (!userAlimyService.isTurnedOn(otherUser.id, AlimyType.PINGPONG)) {
                log.info { "userId: ${otherUser.id} alimyType: ${AlimyType.PINGPONG} 켜져 있지 않아 발송하지 않음" }
                return
            }

            val fcmNotification = FcmNotification(
                title = "${user.getMaskedName()}님이 사진 공개 여부를 선택했어요 📸",
                body = "두근두근, ${user.getMaskedName()}님의 선택을 확인해주세요!"
            )
            fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
        }
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleShareContactEventDto) {
        val bottle = bottleService.findBottleById(event.bottleId)
        val user = userService.findByIdAndNotDeleted(event.userId)
        val otherUser = bottle.findOtherUser(user)

        tabEventService.sendEventByTabType(
            otherUser.id,
            TabEventDto(tabType = TabType.PINGPONG, isNewBadgeVisible = true)
        )

        fcmTokenService.findAllByUserIdAndTokenNotBlank(otherUser.id).forEach {
            if (!userAlimyService.isTurnedOn(otherUser.id, AlimyType.PINGPONG)) {
                log.info { "userId: ${otherUser.id} alimyType: ${AlimyType.PINGPONG} 켜져 있지 않아 발송하지 않음" }
                return
            }

            val fcmNotification = FcmNotification(
                title = "${user.getMaskedName()}님이 최종 선택을 완료했어요 💘",
                body = "두근두근, ${user.getMaskedName()}님의 선택을 확인해주세요!"
            )
            fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
        }
    }
}
