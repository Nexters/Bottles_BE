package com.nexters.bottles.app.notification.component.dto

data class FcmNotification(
    val title: String,
    val body: String,
    val image: String? = null
)
