package com.nexters.bottles.bottle.repository

import com.nexters.bottles.bottle.domain.Bottle
import com.nexters.bottles.bottle.domain.Letter
import com.nexters.bottles.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface LetterRepository : JpaRepository<Letter, Long> {

    fun findByBottleAndUser(bottle: Bottle, user: User): Letter?
}
