package com.nexters.bottles.api.bottle.repository

import com.nexters.bottles.api.bottle.domain.Question
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionRepository : JpaRepository<Question, Long> {
}
