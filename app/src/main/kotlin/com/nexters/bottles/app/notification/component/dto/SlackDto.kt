package com.nexters.bottles.app.notification.component.dto

data class SlackMessage(
    val channel: String,
    val blocks: List<Block>
)

data class Block(
    val type: String,
    val text: Text
)

data class Text(
    val type: String,
    val text: String
)
