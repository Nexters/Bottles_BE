package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.Question
import com.nexters.bottles.app.bottle.repository.QuestionRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionService(
    private val questionRepository: QuestionRepository
) {

    @Cacheable("questions")
    @Transactional(readOnly = true)
    fun findAllQuestions(): List<Question> = questionRepository.findAll()
}
