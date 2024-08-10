package com.nexters.bottles.api.user.component.event

import com.nexters.bottles.api.user.component.event.dto.UserApplicationEventDto
import com.nexters.bottles.app.user.service.UserService
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class UserApplicationEventListener(
    private val userService: UserService,
) {

    private val log = KotlinLogging.logger {  }

    @Async
    @EventListener
    fun handleCustomEvent(event: UserApplicationEventDto) {
        userService.updateLastActivatedAt(event.userId, event.basedAt)
    }
}
