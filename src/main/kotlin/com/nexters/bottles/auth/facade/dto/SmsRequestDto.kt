package com.nexters.bottles.auth.facade.dto

data class SmsRequestDTO(
    val type: String,
    val contentType: String,
    val countryCode: String,
    val from: String,
    val content: String,
    val messages: List<MessageDTO> = arrayListOf()
)
