package com.nexters.bottles.batch.service

import com.nexters.bottles.api.notification.component.FcmClient
import com.nexters.bottles.api.notification.component.dto.FcmNotification
import com.nexters.bottles.api.notification.service.FcmTokenService
import com.nexters.bottles.api.user.service.UserService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class FcmNotificationService(
    private val fcmClient: FcmClient,
    private val fcmTokenService: FcmTokenService,
    private val userService: UserService
) {

    @Scheduled(cron = "0 0 18 * * *")
    fun notifyMatching() {
        val users = userService.findAllByNotDeleted()
        val fcmTokens = fcmTokenService.findByUsers(users)
        val tokens = fcmTokens.map { it.token }

        val fcmNotification = FcmNotification(
            title = "새로운 보틀이 떠내려 왔어요.",
            body = "새로운 보틀을 열어서 확인해보세요. 24시간이 지나면 사라져요!"
        )
        fcmClient.sendNotificationAll(userTokens = tokens, fcmNotification = fcmNotification)
    }
}
