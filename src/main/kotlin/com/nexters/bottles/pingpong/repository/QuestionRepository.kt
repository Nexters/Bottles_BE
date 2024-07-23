package com.nexters.bottles.pingpong.repository

import com.nexters.bottles.pingpong.domain.Question
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionRepository : JpaRepository<Question, Long> {
}
