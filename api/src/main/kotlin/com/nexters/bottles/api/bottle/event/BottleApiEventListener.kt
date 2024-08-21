package com.nexters.bottles.api.bottle.event

import com.nexters.bottles.api.bottle.event.dto.BottleAcceptEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleMatchEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleRefuseEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleRegisterLetterEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleShareContactEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleShareImageEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleStopEventDto
import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.service.BottleHistoryService
import com.nexters.bottles.app.bottle.service.BottleService
import com.nexters.bottles.app.notification.component.FcmClient
import com.nexters.bottles.app.notification.component.dto.FcmNotification
import com.nexters.bottles.app.notification.service.FcmTokenService
import com.nexters.bottles.app.user.service.UserService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class BottleApiEventListener(
    private val bottleService: BottleService,
    private val bottleHistoryService: BottleHistoryService,
    private val fcmTokenService: FcmTokenService,
    private val fcmClient: FcmClient,
    private val userService: UserService,
) {

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleRefuseEventDto) {
        bottleHistoryService.saveRefuseHistory(event.sourceUserId, event.targetUserId)
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleMatchEventDto) {
        bottleHistoryService.saveMatchingHistory(event.sourceUserId, event.targetUserId)
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleAcceptEventDto) {
        val bottle = bottleService.findBottleById(event.bottleId)
        when {
            bottle.isSentLikeMessageAndNotStart() -> {
                fcmTokenService.findAllByUserId(bottle.sourceUser.id).forEach {
                    val fcmNotification = FcmNotification(
                        title = "누군가 ${bottle.sourceUser.name}님에게 편지를 보냈어요! 💘",
                        body = "${bottle.sourceUser.name}님에게 호감을 표현한 사람이 있어요.\n도착한 보틀을 확인해주세요!"
                    )
                    fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
                }
            }

            bottle.isActive() -> {
                fcmTokenService.findAllByUserIds(listOf(bottle.sourceUser.id, bottle.targetUser.id)).forEach {
                    val fcmNotification = FcmNotification(
                        title = "${findOtherUserName(it.userId, bottle)}님과의 문답이 시작됐어요! 💌",
                        body = "어떤 질문이 기다리고 있을까요?\n지금부터 서로를 더 깊게 알아보세요!"
                    )
                    fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
                }
            }
        }
    }

    private fun findOtherUserName(userId: Long, bottle: Bottle): String? {
        return if (userId == bottle.sourceUser.id) bottle.targetUser.name else bottle.sourceUser.name
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleStopEventDto) {
        val bottle = bottleService.findBottleById(event.bottleId)
        val otherUser = bottle.findOtherUser(bottle.stoppedUser!!)

        fcmTokenService.findAllByUserId(otherUser.id).forEach {
            val fcmNotification = FcmNotification(
                title = "아쉬워요! 다른 보틀을 열어볼까요? 😢",
                body = "${bottle.stoppedUser!!.name}님이 대화를 중단했어요.\n대화는 3일 뒤에 삭제돼요."
            )
            fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
        }
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleRegisterLetterEventDto) {
        val bottle = bottleService.findBottleById(event.bottleId)
        val user = userService.findByIdAndNotDeleted(event.userId)
        val otherUser = bottle.findOtherUser(user)

        fcmTokenService.findAllByUserId(otherUser.id).forEach {
            val fcmNotification = FcmNotification(
                title = "${user.name}님이 답변을 완료했어요 👀",
                body = "두근두근, ${user.name}님은 어떻게 생각할까요?\n지금 바로 확인해 보세요!"
            )
            fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
        }
    }

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleShareImageEventDto) {
        val bottle = bottleService.findBottleById(event.bottleId)
        val user = userService.findByIdAndNotDeleted(event.userId)
        val otherUser = bottle.findOtherUser(user)

        fcmTokenService.findAllByUserId(otherUser.id).forEach {
            val fcmNotification = FcmNotification(
                title = "${user.name}님이 사진 공개 여부를 선택했어요 📸",
                body = "두근두근, ${user.name}님의 선택을 확인해주세요!"
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

        fcmTokenService.findAllByUserId(otherUser.id).forEach {
            val fcmNotification = FcmNotification(
                title = "${user.name}님이 최종 선택을 완료했어요 💘",
                body = "두근두근, ${user.name}님의 선택을 확인해주세요!"
            )
            fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
        }
    }
}
