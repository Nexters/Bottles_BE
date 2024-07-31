package com.nexters.bottles.bottle.repository

import com.nexters.bottles.bottle.domain.RandomBottle
import com.nexters.bottles.bottle.domain.enum.RandomBottleStatus
import com.nexters.bottles.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface RandomBottleRepository : JpaRepository<RandomBottle, Long> {

    @Query(
        value = "SELECT rb FROM RandomBottle rb " +
                "JOIN FETCH rb.sourceUser " +
                "WHERE rb.targetUser = :targetUser AND rb.expiredAt > :currentDateTime AND rb.status = :status"
    )
    fun findAllByTargetUserAndStatusAndNotExpired(
        @Param("targetUser") targetUser: User,
        @Param("status") status: RandomBottleStatus,
        @Param("currentDateTime") currentDateTime: LocalDateTime,
    ): List<RandomBottle>
}
