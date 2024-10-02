package com.nexters.bottles.app.bottle.domain.vo

import java.util.regex.Pattern
import javax.persistence.Embeddable

@Embeddable
class LikeMessage(
    val value: String
) {

    fun getLikeEmoji(): String {
        val matcher = EMOJI_PATTERN.matcher(this.value)

        return matcher.results()
            .map { it.group() }
            .toList()
            .lastOrNull() ?: throw IllegalStateException("이모지를 찾을 수 없습니다.")
    }

    companion object {
        private val EMOJI_PATTERN = Pattern.compile(
            "[\\p{So}\\p{Cn}\\p{Sc}\\p{Sk}\\p{Sm}\\p{Zs}]",
            Pattern.UNICODE_CHARACTER_CLASS
        )
    }
}
