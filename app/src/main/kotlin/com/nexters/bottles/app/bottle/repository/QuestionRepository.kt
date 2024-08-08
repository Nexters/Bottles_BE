package com.nexters.bottles.app.bottle.repository

import com.nexters.bottles.app.bottle.domain.Question
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionRepository : JpaRepository<Question, Long> {
}
