package com.nexters.bottles.app.bottle.repository

import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.Letter
import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LetterRepository : JpaRepository<Letter, Long> {

    fun findByBottleAndUser(bottle: Bottle, user: User): Letter?

    fun findAllByUserId(userId: Long): List<Letter>

    @Query(
        value = "SELECT l FROM Letter l " +
                "JOIN Bottle b " +
                "ON l.bottle = b AND b.pingPongStatus IN :pingPongStatus "
    )
    fun findAllByPingPongStatus(
        @Param("pingPongStatus") pingPongStatus: Set<PingPongStatus>
    ): List<Letter>
}
