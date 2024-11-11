package com.nexters.bottles.app.user.component.event

import com.nexters.bottles.app.auth.event.SignUpEventDto
import com.nexters.bottles.app.bottle.service.BottleHistoryService
import com.nexters.bottles.app.bottle.service.BottleService
import com.nexters.bottles.app.common.component.FileService
import com.nexters.bottles.app.user.component.event.dto.IntroductionSaveEventDto
import com.nexters.bottles.app.user.component.event.dto.UploadImageEventDto
import com.nexters.bottles.app.user.service.UserService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserProfileApplicationEventListener(
    private val userService: UserService,
    private val bottleService: BottleService,
    private val bottleHistoryService: BottleHistoryService,
    private val fileService: FileService
) {

    @Async
    @EventListener
    fun handleCustomEvent(event: SignUpEventDto) {
        var savedBottles = bottleService.matchFirstRandomBottle(event.userId, 3)
        savedBottles.forEach {
            bottleHistoryService.saveMatchingHistory(sourceUserId = it.sourceUser.id, targetUserId = it.targetUser.id)
        }
    }

    @Async
    @TransactionalEventListener
    fun handleCustomEvent(event: UploadImageEventDto) {
        if (event.prevImageUrls.isEmpty()) return
        event.prevImageUrls.forEach {
            fileService.remove(it)
        }
        event.prevBlurredImageUrl?.let { fileService.remove(it) }
    }
}
