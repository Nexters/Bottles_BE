package com.nexters.bottles.app.bottle.repository

import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.Letter
import com.nexters.bottles.app.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface LetterRepository : JpaRepository<Letter, Long> {

    fun findByBottleAndUser(bottle: Bottle, user: User): Letter?

    fun findAllByUserId(userId: Long): List<Letter>
}
