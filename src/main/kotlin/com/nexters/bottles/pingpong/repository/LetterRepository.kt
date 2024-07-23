package com.nexters.bottles.pingpong.repository

import com.nexters.bottles.pingpong.domain.Letter
import org.springframework.data.jpa.repository.JpaRepository

interface LetterRepository : JpaRepository<Letter, Long> {
}
