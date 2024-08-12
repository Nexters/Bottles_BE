package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.Letter
import com.nexters.bottles.app.bottle.domain.LetterQuestionAndAnswer
import com.nexters.bottles.app.bottle.domain.Question
import com.nexters.bottles.app.bottle.repository.BottleRepository
import com.nexters.bottles.app.bottle.repository.LetterRepository
import com.nexters.bottles.app.user.domain.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class LetterService(
    private val letterRepository: LetterRepository,
    private val bottleRepository: BottleRepository
) {

    @Transactional(readOnly = true)
    fun findLetter(bottle: Bottle, user: User): Letter {
        return letterRepository.findByBottleAndUser(bottle, user) ?: throw IllegalArgumentException("고객센터에 문의해주세요")
    }

    @Transactional
    fun saveLetter(bottleId: Long, questions: List<Question>) {
        val bottle = bottleRepository.findByIdOrNull(bottleId) ?: throw IllegalArgumentException("고객센터에 문의해주세요")
        val letters = questions.map {
            LetterQuestionAndAnswer(question = it.question)
        }
        val sourceUserLetter = Letter(bottle = bottle, user = bottle.sourceUser, letters = letters)
        val targetUserLetter = Letter(bottle = bottle, user = bottle.targetUser, letters = letters)
        letterRepository.saveAll(listOf(sourceUserLetter, targetUserLetter))
    }

    @Transactional
    fun registerLetter(bottle: Bottle, user: User, order: Int, answer: String) {
        val letter =
            letterRepository.findByBottleAndUser(bottle, user) ?: throw IllegalArgumentException("고객센터에 문의해주세요")
        letter.registerAnswer(order, answer)
        letter.markUnread()
    }

    @Transactional
    fun markReadOtherUserLetter(bottle: Bottle, otherUser: User) {
        val otherUserLetter = letterRepository.findByBottleAndUser(bottle, otherUser)
            ?: throw IllegalArgumentException("고객센터에 문의해주세요")
        otherUserLetter.markRead()
    }

    @Transactional
    fun shareImage(bottle: Bottle, user: User, willShare: Boolean) {
        val letter =
            letterRepository.findByBottleAndUser(bottle, user) ?: throw IllegalArgumentException("고객센터에 문의해주세요")
        letter.shareImage(willShare)
        letter.markUnread()

        if (!willShare) {
            letter.stopPingPong(user, LocalDateTime.now())
        }
    }

    @Transactional
    fun shareContact(bottle: Bottle, user: User, willShare: Boolean) {
        val letter =
            letterRepository.findByBottleAndUser(bottle, user) ?: throw IllegalArgumentException("고객센터에 문의해주세요")
        letter.shareContact(willShare)
        letter.markUnread()

        if (!willShare) {
            letter.stopPingPong(user, LocalDateTime.now())
        } else {
            letter.finishIfAllShare()
        }
    }
}
