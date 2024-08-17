package com.nexters.bottles.api.auth.component

import com.nexters.bottles.api.auth.component.event.DeleteUserEventDto
import com.nexters.bottles.app.bottle.service.BottleCachingService
import com.nexters.bottles.app.bottle.service.BottleService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AuthApplicationEventListener(
    private val bottleService: BottleService,
    private val bottleCachingService: BottleCachingService
) {

    @Async
    @EventListener
    fun handleCustomEvent(event: DeleteUserEventDto) {
        val pingPongBottles = bottleService.getPingPongBottles(event.userId)
        pingPongBottles.forEach {
            bottleCachingService.stop(userId = event.userId, bottleId = it.id)
        }
    }
}
