package com.nexters.bottles.bottle.repository

import com.nexters.bottles.bottle.domain.Letter
import org.springframework.data.jpa.repository.JpaRepository

interface LetterRepository : JpaRepository<Letter, Long> {
}
