package com.nexters.bottles.batch.scheduler.dto

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
