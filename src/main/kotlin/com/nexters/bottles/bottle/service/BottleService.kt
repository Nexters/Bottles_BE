package com.nexters.bottles.bottle.service

import com.nexters.bottles.bottle.domain.Bottle
import com.nexters.bottles.bottle.domain.Letter
import com.nexters.bottles.bottle.domain.LetterQuestionAndAnswer
import com.nexters.bottles.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.bottle.repository.BottleRepository
import com.nexters.bottles.bottle.repository.LetterRepository
import com.nexters.bottles.bottle.repository.QuestionRepository
import com.nexters.bottles.user.domain.User
import com.nexters.bottles.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BottleService(
    private val bottleRepository: BottleRepository,
    private val userRepository: UserRepository,
    private val letterRepository: LetterRepository,
    private val questionRepository: QuestionRepository
) {

    @Transactional(readOnly = true)
    fun getBottles(): List<Bottle> {
        // TODO User 회원 가입 기능 구현후 수정
        val user = userRepository.findByIdOrNull(1L) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        return bottleRepository.findByTargetUserAndStatusAndNotExpired(user, PingPongStatus.NONE, LocalDateTime.now())
    }

    @Transactional(readOnly = true)
    fun getBottle(bottleId: Long): Bottle {
        return bottleRepository.findByIdAndStatusAndNotExpired(bottleId, PingPongStatus.NONE, LocalDateTime.now())
            ?: throw IllegalArgumentException("이미 떠내려간 보틀이에요")
    }

    @Transactional
    fun acceptBottle(bottleId: Long) {
        val bottle = bottleRepository.findByIdAndStatusAndNotExpired(bottleId, PingPongStatus.NONE, LocalDateTime.now())
            ?: throw IllegalArgumentException("이미 떠내려간 보틀이에요")

        // TODO User 회원 가입 기능 구현후 수정
        val targetUser = userRepository.findByIdOrNull(1L) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
        val sourceUser = userRepository.findByIdOrNull(bottle.sourceUser.id)
            ?: throw IllegalArgumentException("탈퇴한 회원이에요")

        bottle.accept()

        val letters = findRandomQuestions()
        saveLetter(bottle, targetUser, letters)
        saveLetter(bottle, sourceUser, letters)
    }

    private fun findRandomQuestions() = questionRepository.findAll()
        .shuffled()
        .take(3)
        .map {
            LetterQuestionAndAnswer(question = it.question)
        }

    private fun saveLetter(
        bottle: Bottle,
        user: User,
        letters: List<LetterQuestionAndAnswer>
    ) {
        val letter = Letter(bottle = bottle, user = user, letters = letters)
        letterRepository.save(letter)
    }

    @Transactional
    fun refuseBottle(bottleId: Long) {
        val bottle = bottleRepository.findByIdAndStatusAndNotExpired(bottleId, PingPongStatus.NONE, LocalDateTime.now())
            ?: throw IllegalArgumentException("이미 떠내려간 보틀이에요")

        // TODO User 회원 가입 기능 구현후 수정
        val targetUser = userRepository.findByIdOrNull(1L) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
        userRepository.findByIdOrNull(bottle.sourceUser.id) ?: throw IllegalArgumentException("탈퇴한 회원이에요")

        bottle.refuse(targetUser)
    }

    @Transactional(readOnly = true)
    fun getPingPongBottles(): List<Bottle> {
        // TODO User 회원 가입 기능 구현후 수정
        val user = userRepository.findByIdOrNull(1L) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        return bottleRepository.findPingPongBottlesByUser(user)
    }
}
