package com.nexters.bottles.app.bottle.repository

import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.BottleReadHistory
import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BottleReadHistoryRepository : JpaRepository<BottleReadHistory, Long> {

    fun findByBottleAndUser(bottle: Bottle, user: User): BottleReadHistory

    @Query(
        value = "SELECT brh FROM BottleReadHistory brh " +
                "JOIN Bottle b " +
                "ON brh.bottle = b AND b.bottleStatus = :bottleStatus " +
                "AND (b.targetUser = :user OR b.sourceUser = :user) " +
                "WHERE brh.user = :user"
    )
    fun findAllByUserAndBottleStatus(
        @Param("user") user: User,
        @Param("bottleStatus") bottleStatus: BottleStatus
    ): List<BottleReadHistory>
}
