package com.nexters.bottles.pingpong.repository

import com.nexters.bottles.pingpong.domain.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface QuestionRepository : JpaRepository<Question, Long> {

    @Query(
        value = "SELECT * FROM Question " +
                "ORDER BY RAND() " +
                "LIMIT :numberOfQuestion",
        nativeQuery = true
    )
    fun findByRandom(@Param("numberOfQuestion") numberOfQuestion: Int): List<Question>
}
