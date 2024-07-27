package com.nexters.bottles.auth.facade.dto

data class NaverSmsRequestDTO(
    var type: String = "SMS",
    var contentType: String = "COMM",
    var countryCode: String = "82",
    var from: String,
    var content: String,
    var messages: List<MessageDTO> = arrayListOf()
)

data class MessageDTO(
    var to: String,
    var content: String,
)
