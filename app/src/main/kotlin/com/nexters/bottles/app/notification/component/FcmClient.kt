package com.nexters.bottles.app.notification.component

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import com.nexters.bottles.app.notification.component.dto.FcmNotification
import com.nexters.bottles.app.notification.repository.FcmTokenRepository
import mu.KotlinLogging
import org.springframework.stereotype.Component


@Component
class FcmClient(
    private val fcmTokenRepository: FcmTokenRepository,
) {

    private val log = KotlinLogging.logger { }

    fun sendNotificationTo(userToken: String, fcmNotification: FcmNotification): String? {
        val notification = makeNotification(fcmNotification)
        val message = Message.builder()
            .setNotification(notification)
            .setToken(userToken)
            .build()

        try {
            return FirebaseMessaging.getInstance().send(message)
        } catch (e: Exception) {
            fcmTokenRepository.deleteByToken(userToken)
            log.info { "fcmToken 404 예외 - 더이상 유효하지 않아 삭제 $userToken" }
            return null
        }
    }

    fun sendNotificationAll(userTokens: List<String>, fcmNotification: FcmNotification) {
        val notification = makeNotification(fcmNotification)
        val message = MulticastMessage.builder()
            .setNotification(notification)
            .addAllTokens(userTokens)
            .build()
        val response = FirebaseMessaging.getInstance().sendEachForMulticast(message)
    }

    fun sendDataTo(userToken: String, data: Map<String, String>) {
        val message = Message.builder()
            .putAllData(data)
            .setToken(userToken)
            .build()
        val response = FirebaseMessaging.getInstance().send(message)
    }

    fun sendDataAll(userTokens: List<String>, data: Map<String, String>) {
        val message = MulticastMessage.builder()
            .putAllData(data)
            .addAllTokens(userTokens)
            .build()
        val response = FirebaseMessaging.getInstance().sendEachForMulticast(message)
    }

    private fun makeNotification(fcmNotification: FcmNotification): Notification =
        Notification.builder()
            .setTitle(fcmNotification.title)
            .setBody(fcmNotification.body)
            .setImage(fcmNotification.image)
            .build()
}
