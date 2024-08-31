package com.nexters.bottles.app.user.component.event

import com.nexters.bottles.app.bottle.service.BottleHistoryService
import com.nexters.bottles.app.bottle.service.BottleService
import com.nexters.bottles.app.user.component.event.dto.IntroductionSaveEventDto
import com.nexters.bottles.app.user.service.UserService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserProfileApplicationEventListener(
    private val userService: UserService,
    private val bottleService: BottleService,
    private val bottleHistoryService: BottleHistoryService
) {

    @Async
    @TransactionalEventListener
    fun handleCustomEvent(event: IntroductionSaveEventDto) {
        val user = userService.findByIdAndNotDeleted(event.userId)
        bottleService.matchFirstRandomBottle(user)?.let {
            bottleHistoryService.saveMatchingHistory(sourceUserId = it.sourceUser.id, targetUserId = it.targetUser.id)
        }
    }
}
