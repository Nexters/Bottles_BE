package com.nexters.bottles.bottle.repository

import com.nexters.bottles.bottle.domain.Bottle
import com.nexters.bottles.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface BottleRepository : JpaRepository<Bottle, Long> {

    @Query(
        value = "SELECT b FROM Bottle b " +
                "JOIN FETCH b.sourceUser " +
                "WHERE b.targetUser = :targetUser AND b.expiredAt > :currentDateTime AND b.pingPongStatus = :pingPongStatus"
    )
    fun findByTargetUserAndStatusAndNotExpired(
        @Param("targetUser") targetUser: User,
        @Param("pingPongStatus") pingPongStatus: PingPongStatus,
        @Param("currentDateTime") currentDateTime: LocalDateTime
    ): List<Bottle>

    @Query(
        value = "SELECT b FROM Bottle b " +
                "JOIN FETCH b.sourceUser " +
                "WHERE b.id = :bottleId AND b.expiredAt > :currentDateTime AND b.pingPongStatus IN :pingPongStatus"
    )
    fun findByIdAndStatusAndNotExpired(
        @Param("bottleId") bottleId: Long,
        @Param("pingPongStatus") pingPongStatus: Set<PingPongStatus>,
        @Param("currentDateTime") currentDateTime: LocalDateTime
    ): Bottle?

    @Query(
        value = "SELECT b FROM Bottle b " +
                "WHERE (b.targetUser = :user OR b.sourceUser = :user) " +
                "AND b.pingPongStatus IN :pingPongStatus"
    )
    fun findByUserAndStatus(
        @Param("user") user: User,
        @Param("pingPongStatus") pingPongStatus: Set<PingPongStatus>
    ): List<Bottle>

    @Query(
        value = "SELECT b FROM Bottle b " +
                "WHERE b.id = :bottleId AND b.pingPongStatus IN :pingPongStatus"
    )
    fun findByIdAndStatus(
        @Param("bottleId") bottleId: Long,
        @Param("pingPongStatus") pingPongStatus: Set<PingPongStatus>
    ): Bottle?
}
