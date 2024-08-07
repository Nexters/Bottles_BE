package com.nexters.bottles.api.notification.facade

import com.nexters.bottles.api.notification.facade.dto.FcmTokenRegisterRequest
import com.nexters.bottles.api.notification.service.FcmTokenService
import com.nexters.bottles.api.user.service.UserService
import org.springframework.stereotype.Component

@Component
class FcmTokenFacade(
    private val userService: UserService,
    private val fcmTokenService: FcmTokenService,
) {

    fun registerFcmToken(userId: Long, fcmTokenRegisterRequest: FcmTokenRegisterRequest) {
        val user = userService.findByIdAndNotDeleted(userId)
        fcmTokenService.registerFcmToken(user, fcmTokenRegisterRequest.token)
    }
}
