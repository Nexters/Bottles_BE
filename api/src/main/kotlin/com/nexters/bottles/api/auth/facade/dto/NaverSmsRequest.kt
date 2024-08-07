package com.nexters.bottles.api.auth.facade.dto

data class NaverSmsRequest(
    var type: String = "SMS",
    var contentType: String = "COMM",
    var countryCode: String = "82",
    var from: String,
    var content: String,
    var messages: List<MessageDto> = arrayListOf()
)

data class MessageDto(
    var to: String,
    var content: String,
)
