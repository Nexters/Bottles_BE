package com.nexters.bottles.api.auth.facade.dto

data class SmsRequestDTO(
    val type: String,
    val contentType: String,
    val countryCode: String,
    val from: String,
    val content: String,
    val messages: List<com.nexters.bottles.api.auth.facade.dto.MessageDTO> = arrayListOf()
)
