package com.nexters.bottles.app.notification.component

import com.nexters.bottles.app.notification.component.dto.FcmNotification
import com.nexters.bottles.app.notification.service.FcmTokenService
import com.nexters.bottles.app.user.service.UserService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class FcmNotificationScheduler(
    private val fcmClient: FcmClient,
    private val fcmTokenService: FcmTokenService,
    private val userService: UserService
) {

    @Scheduled(cron = "0 0 18 * * *")
    fun notifyMatching() {
        val userIds = userService.findAllByNotDeleted().map { it.id }
        val fcmTokens = fcmTokenService.findAllByUserIdsAndTokenNotBlank(userIds)
        val tokens = fcmTokens.map { it.token }

        val fcmNotification = FcmNotification(
            title = "새로운 보틀이 떠내려왔어요 🏖️",
            body = "모래사장에 새로운 보틀이 도착했어요.\n도착한 보틀을 확인해주세요!"
        )
        fcmClient.sendNotificationAll(userTokens = tokens, fcmNotification = fcmNotification)
    }
}
