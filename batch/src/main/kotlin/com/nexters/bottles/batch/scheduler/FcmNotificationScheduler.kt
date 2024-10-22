package com.nexters.bottles.batch.scheduler

import com.nexters.bottles.app.notification.component.FcmClient
import com.nexters.bottles.app.notification.component.dto.FcmNotification
import com.nexters.bottles.app.notification.service.FcmTokenService
import com.nexters.bottles.app.user.domain.enum.AlimyType
import com.nexters.bottles.app.user.service.UserAlimyService
import com.nexters.bottles.app.user.service.UserService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 실질적으로 돌고 있지 않다.
 */
@Component
class FcmNotificationScheduler(
    private val fcmClient: FcmClient,
    private val fcmTokenService: FcmTokenService,
    private val userService: UserService,
    private val userAlimyService: UserAlimyService,
) {

    @Scheduled(cron = "0 0 18 * * *")
    fun notifyMatching() {
        val userIds = userService.findAllByDeletedFalseAndMatchActivatedTrue().map { it.id }
        val alimyAllowUserIds = userAlimyService.findAllowedUserAlimyByUserIdsAndAlimyType(userIds.toSet(), AlimyType.DAILY_RANDOM)
            .map { it.userId }

        val fcmTokens = fcmTokenService.findAllByUserIdsAndTokenNotBlank(alimyAllowUserIds)
        val tokens = fcmTokens.map { it.token }

        val fcmNotification = FcmNotification(
            title = "새로운 보틀이 떠내려왔어요 🏖️",
            body = "모래사장에 새로운 보틀이 도착했어요.\n도착한 보틀을 확인해주세요!"
        )
        fcmClient.sendNotificationAll(userTokens = tokens, fcmNotification = fcmNotification)
    }
}
