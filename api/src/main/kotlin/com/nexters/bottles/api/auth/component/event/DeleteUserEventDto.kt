package com.nexters.bottles.api.auth.component.event

data class DeleteUserEventDto(
    val userId: Long,
    val accessToken: String
)
