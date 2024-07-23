package com.nexters.bottles.bottle.repository

import com.nexters.bottles.bottle.domain.Question
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionRepository : JpaRepository<Question, Long> {
}
