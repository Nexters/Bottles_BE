package com.nexters.bottles.app.auth.event

data class DeleteUserEventDto(
    val userId: Long,
    val accessToken: String
)
