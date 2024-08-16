package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.Question
import com.nexters.bottles.app.bottle.repository.QuestionRepository
import com.nexters.bottles.app.config.CacheType.Name.LETTER_QUESTION
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionCachingService(
    private val questionRepository: QuestionRepository
) {

    @Cacheable(LETTER_QUESTION)
    @Transactional(readOnly = true)
    fun findAllQuestions(): List<Question> = questionRepository.findAll()
}
