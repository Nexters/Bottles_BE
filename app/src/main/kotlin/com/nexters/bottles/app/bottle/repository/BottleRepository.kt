package com.nexters.bottles.app.bottle.repository

import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface BottleRepository : JpaRepository<Bottle, Long> {

    @Query(
        value = "SELECT b FROM Bottle b " +
                "WHERE b.targetUser = :targetUser AND b.expiredAt > :currentDateTime AND b.pingPongStatus = :pingPongStatus " +
                "AND b.deleted = false AND b.targetUser.deleted = false AND b.sourceUser.deleted = false "
    )
    fun findAllByTargetUserAndStatusAndNotExpiredAndDeletedFalse(
        @Param("targetUser") targetUser: User,
        @Param("pingPongStatus") pingPongStatus: PingPongStatus,
        @Param("currentDateTime") currentDateTime: LocalDateTime
    ): List<Bottle>

    @Query(
        value = "SELECT b FROM Bottle b " +
                "WHERE b.targetUser = :targetUser AND b.expiredAt > :currentDateTime AND b.bottleStatus IN :bottleStatus " +
                "AND b.deleted = false AND b.targetUser.deleted = false AND b.sourceUser.deleted = false "
    )
    fun findAllByTargetUserAndBottleStatusAndNotExpiredAndDeletedFalse(
        @Param("targetUser") targetUser: User,
        @Param("bottleStatus") bottleStatus: Set<BottleStatus>,
        @Param("currentDateTime") currentDateTime: LocalDateTime
    ): List<Bottle>

    @Query(
        value = "SELECT b FROM Bottle b " +
                "WHERE b.id = :bottleId AND b.expiredAt > :currentDateTime AND b.pingPongStatus IN :pingPongStatus " +
                "AND b.deleted = false AND b.targetUser.deleted = false AND b.sourceUser.deleted = false "
    )
    fun findByIdAndStatusAndNotExpiredAndDeletedFalse(
        @Param("bottleId") bottleId: Long,
        @Param("pingPongStatus") pingPongStatus: Set<PingPongStatus>,
        @Param("currentDateTime") currentDateTime: LocalDateTime
    ): Bottle?

    @Query(
        value = "SELECT b FROM Bottle b " +
                "WHERE (b.targetUser = :user OR b.sourceUser = :user) " +
                "AND b.targetUser.deleted = false " +
                "AND b.sourceUser.deleted = false " +
                "AND b.pingPongStatus IN :pingPongStatus " +
                "AND b.deleted = false "
    )
    fun findAllByNotDeletedUserAndStatusAndDeletedFalse(
        @Param("user") user: User,
        @Param("pingPongStatus") pingPongStatus: Set<PingPongStatus>
    ): List<Bottle>

    @Query(
        value = "SELECT b FROM Bottle b " +
                "WHERE (b.targetUser = :user OR b.sourceUser = :user) " +
                "AND b.pingPongStatus IN :pingPongStatus " +
                "AND b.deleted = false "
    )
    fun findAllByUserAndStatusAndDeletedFalse(
        @Param("user") user: User,
        @Param("pingPongStatus") pingPongStatus: Set<PingPongStatus>
    ): List<Bottle>

    @Query(
        value = "SELECT b FROM Bottle b " +
                "WHERE b.id = :bottleId AND b.pingPongStatus IN :pingPongStatus AND b.deleted = false " +
                "AND b.targetUser.deleted = false AND b.sourceUser.deleted = false "
    )
    fun findByIdAndStatusAndDeletedFalse(
        @Param("bottleId") bottleId: Long,
        @Param("pingPongStatus") pingPongStatus: Set<PingPongStatus>
    ): Bottle?

    fun findAllByTargetUser(user: User): List<Bottle>

    fun findAllBySourceUser(user: User): List<Bottle>

    @Modifying
    @Query(value = "UPDATE Bottle b SET b.deleted = true, b.deleted = :deletedAt WHERE b.stoppedAt < :stoppedAt")
    fun updateAllDeletedTrueAndDeletedAtByStoppedAtBefore(
        @Param("stoppedAt") stoppedAt: LocalDateTime,
        @Param("deletedAt") deletedAt: LocalDateTime
    ): List<Bottle>


    fun findAllByCreatedAtGreaterThanAndCreatedAtLessThan(yesterday: LocalDateTime, today: LocalDateTime): List<Bottle>
}
