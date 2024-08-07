package com.nexters.bottles.api.bottle.repository

import com.nexters.bottles.api.bottle.domain.Bottle
import com.nexters.bottles.api.bottle.domain.Letter
import com.nexters.bottles.api.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface LetterRepository : JpaRepository<Letter, Long> {

    fun findByBottleAndUser(bottle: Bottle, user: User): Letter?

    fun findAllByUserId(userId: Long): List<Letter>
}
