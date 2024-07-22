package com.nexters.bottles.bottle.repository

import com.nexters.bottles.bottle.domain.Bottle
import com.nexters.bottles.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BottleRepository : JpaRepository<Bottle, Long> {

    @Query(
        value = "SELECT b FROM Bottle b " +
                "JOIN FETCH b.sourceUser " +
                "WHERE b.targetUser = :targetUser AND b.expiredAt > CURRENT_TIMESTAMP"
    )
    fun findByTargetUserAndNotExpired(@Param("targetUser") targetUser: User): List<Bottle>
}
