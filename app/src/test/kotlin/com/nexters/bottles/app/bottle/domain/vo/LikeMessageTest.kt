package com.nexters.bottles.app.bottle.domain.vo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class LikeMessageTest {

    @ParameterizedTest
    @CsvSource(
        "반가워요 🥰,🥰",
        "반가워요 🥳,🥳",
        "반가워요 😉,😉",
        "반가워요 😎,😎",
        "반가🥰워요 😎,😎",
    )
    fun `호감 메세지에서 가장 마지막 이모티콘을 파싱한다`(input: String, expected: String) {
        val likeMessage = LikeMessage(input)
        val likeEmoji = likeMessage.getLikeEmoji()

        assertThat(likeEmoji).isEqualTo(expected)
    }
}
