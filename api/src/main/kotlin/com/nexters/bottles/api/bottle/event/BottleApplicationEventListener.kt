package com.nexters.bottles.api.bottle.event

import com.nexters.bottles.api.bottle.event.dto.BottleApplicationEventDto
import com.nexters.bottles.app.bottle.service.BottleHistoryService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class BottleApplicationEventListener(
    private val bottleHistoryService: BottleHistoryService
) {

    @Async
    @EventListener
    fun handleCustomEvent(event: BottleApplicationEventDto) {
        when (event.isRefused) {
            true -> bottleHistoryService.saveRefuseHistory(event.sourceUserId, event.targetUserId)
            false -> bottleHistoryService.saveMatchingHistory(event.sourceUserId, event.targetUserId)
        }
    }
}
