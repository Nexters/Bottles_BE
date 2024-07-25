package com.nexters.bottles.bottle.service

import com.nexters.bottles.bottle.domain.Bottle
import com.nexters.bottles.bottle.domain.Letter
import com.nexters.bottles.bottle.repository.LetterRepository
import com.nexters.bottles.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LetterService(
    private val letterRepository: LetterRepository,
) {

    @Transactional(readOnly = true)
    fun findLetter(bottle: Bottle, user: User): Letter {
        return letterRepository.findByBottleAndUser(bottle, user) ?: throw IllegalArgumentException("고객센터에 문의해주세요")
    }

    @Transactional
    fun registerLetter(bottle: Bottle, user: User, order: Int, answer: String) {
        val letter =
            letterRepository.findByBottleAndUser(bottle, user) ?: throw IllegalArgumentException("고객센터에 문의해주세요")
        letter.registerAnswer(order, answer)
        letter.markUnread()
    }
}
