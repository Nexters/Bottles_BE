package com.nexters.bottles.notification.controller

import com.nexters.bottles.global.interceptor.AuthRequired
import com.nexters.bottles.global.resolver.AuthUserId
import com.nexters.bottles.notification.facade.FcmTokenFacade
import com.nexters.bottles.notification.facade.dto.FcmTokenRegisterRequest
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notification")
class FcmTokenController(
    private val fcmTokenFacade: FcmTokenFacade
) {

    @ApiOperation("FCM 토큰 저장하기")
    @PostMapping("/fcm-token")
    @AuthRequired
    fun registerFcmToken(@AuthUserId userId: Long, @RequestBody fcmTokenRegisterRequest: FcmTokenRegisterRequest) {
        fcmTokenFacade.registerFcmToken(userId, fcmTokenRegisterRequest)
    }
}
