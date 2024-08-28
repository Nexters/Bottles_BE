package com.nexters.bottles.api.auth.event

import com.nexters.bottles.api.auth.event.dto.SignUpEventDto
import com.nexters.bottles.api.bottle.event.dto.BottleStopEventDto
import com.nexters.bottles.app.notification.component.FcmClient
import com.nexters.bottles.app.notification.component.dto.FcmNotification
import com.nexters.bottles.app.notification.service.FcmTokenService
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AuthApiEventListener(
    private val fcmTokenService: FcmTokenService,
    private val fcmClient: FcmClient,
) {

    private val log = KotlinLogging.logger {  }

    @Async
    @EventListener
    fun handleCustomEvent(event: SignUpEventDto) {
        val fcmNotification = FcmNotification(
            title = "똑똑똑! ${event.userName}님에게 보틀이 도착했어요 \uD83D\uDC40",
            body = "보틀을 확인하기 위해서는 자기소개 작성이 꼭 필요해요!\n" +
                    "자기소개를 작성하러 가볼까요?"
        )

        fcmTokenService.findAllByUserIdAndTokenNotBlank(userId = event.userId).forEach {
            fcmClient.sendNotificationTo(userToken = it.token, fcmNotification = fcmNotification)
            log.info { "[SignUpEventDto] 회원 가입 후 자기소개 작성 부스팅 token: ${it.token}" }
        }
    }
}
