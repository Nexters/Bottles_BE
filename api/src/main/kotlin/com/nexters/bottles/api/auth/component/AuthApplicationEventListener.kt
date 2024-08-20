package com.nexters.bottles.api.auth.component

import com.nexters.bottles.api.auth.component.event.DeleteUserEventDto
import com.nexters.bottles.app.auth.service.BlackListService
import com.nexters.bottles.app.auth.service.RefreshTokenService
import com.nexters.bottles.app.bottle.service.BottleCachingService
import com.nexters.bottles.app.bottle.service.BottleService
import com.nexters.bottles.app.notification.service.FcmTokenService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AuthApplicationEventListener(
    private val bottleService: BottleService,
    private val bottleCachingService: BottleCachingService,
    private val fcmTokenService: FcmTokenService,
    private val blackListService: BlackListService,
    private val refreshTokenService: RefreshTokenService,
) {

    @Async
    @EventListener
    fun handleCustomEvent(event: DeleteUserEventDto) {
        blackListService.add(event.accessToken)
        refreshTokenService.delete(event.userId)

        val pingPongBottles = bottleService.getPingPongBottles(event.userId)
        pingPongBottles.forEach {
            val stoppedBottle = bottleService.stop(userId = event.userId, bottleId = it.id)
            bottleCachingService.evictPingPongList(stoppedBottle.sourceUser.id, stoppedBottle.targetUser.id)
        }

        fcmTokenService.deleteAllFcmTokenByUserId(event.userId)
    }
}
