package com.nexters.bottles.pingpong.service

import com.nexters.bottles.pingpong.domain.Letter
import com.nexters.bottles.pingpong.domain.LetterQuestionAndAnswer
import com.nexters.bottles.pingpong.domain.PingPong
import com.nexters.bottles.pingpong.repository.LetterRepository
import com.nexters.bottles.pingpong.repository.PingPongRepository
import com.nexters.bottles.pingpong.repository.QuestionRepository
import com.nexters.bottles.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PingPongService(
    private val pingPongRepository: PingPongRepository,
    private val letterRepository: LetterRepository,
    private val questionRepository: QuestionRepository
) {

    @Transactional
    fun startPingPong(userA: User, userB: User) {
        val pingPong = PingPong(userA = userA, userB = userB)
        val savedPingPong = pingPongRepository.save(pingPong)

        val letters = questionRepository.findByRandom(3)
            .map {
                LetterQuestionAndAnswer(question = it.question)
            }
        val userALetter = Letter(pingPong = savedPingPong, user = userA, letters = letters)
        val userBLetter = Letter(pingPong = savedPingPong, user = userB, letters = letters)
        letterRepository.save(userALetter)
        letterRepository.save(userBLetter)
    }
}
