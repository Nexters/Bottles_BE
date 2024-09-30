package com.nexters.bottles.app.bottle.domain

import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.bottle.domain.vo.LikeMessage
import com.nexters.bottles.app.common.BaseEntity
import com.nexters.bottles.app.user.domain.User
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Bottle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_user_id")
    var targetUser: User,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_user_id")
    var sourceUser: User,

    @Embedded
    var likeMessage: LikeMessage? = null,

    var expiredAt: LocalDateTime = LocalDateTime.now().plusDays(1),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stopped_user_id")
    var stoppedUser: User? = null,

    var stoppedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "first_select_user_id")
    var firstSelectUser: User? = null,

    @Enumerated(value = EnumType.STRING)
    var bottleStatus: BottleStatus = BottleStatus.RANDOM,

    @Enumerated(value = EnumType.STRING)
    var pingPongStatus: PingPongStatus = PingPongStatus.NONE,

    var deleted: Boolean = false,

    var deletedAt: LocalDateTime? = null,
) : BaseEntity() {

    fun sendLikeMessage(from: User, to: User, likeMessage: String, now: LocalDateTime) {
        this.targetUser = to
        this.sourceUser = from
        this.likeMessage = LikeMessage(likeMessage)
        this.bottleStatus = BottleStatus.SENT
        this.expiredAt = now.plusDays(1)
    }

    fun startPingPong() {
        this.pingPongStatus = PingPongStatus.ACTIVE
    }

    fun refuse(refusedBy: User) {
        this.stoppedUser = refusedBy
        this.pingPongStatus = PingPongStatus.REFUSED
    }

    fun findOtherUser(user: User): User {
        return when (user.id) {
            targetUser.id -> sourceUser
            sourceUser.id -> targetUser
            else -> throw IllegalArgumentException("고객센터에 문의해주세요")
        }
    }

    fun findOtherUserId(userId: Long): Long {
        return when (userId) {
            targetUser.id -> sourceUser.id
            sourceUser.id -> targetUser.id
            else -> throw IllegalArgumentException("고객센터에 문의해주세요")
        }
    }

    fun stop(stoppedBy: User, stoppedAt: LocalDateTime) {
        this.stoppedUser = stoppedBy
        this.pingPongStatus = PingPongStatus.STOPPED
        this.stoppedAt = stoppedAt
    }

    fun hasFirstSelectUser(): Boolean {
        return firstSelectUser != null
    }

    fun markFirstSelectUser(user: User) {
        firstSelectUser = user
    }

    fun match() {
        pingPongStatus = PingPongStatus.MATCHED
    }

    fun isStopped(): Boolean {
        return pingPongStatus == PingPongStatus.STOPPED
    }

    fun isActive(): Boolean {
        return pingPongStatus == PingPongStatus.ACTIVE
    }

    fun calculateDeletedAfterDays(): Long? {
        if (stoppedAt == null) return null

        val now = LocalDateTime.now()
        val daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), stoppedAt!!.toLocalDate());
        return DELETE_AFTER_DAYS - daysBetween
    }

    fun isSentLikeMessageAndNotStart(): Boolean {
        return bottleStatus == BottleStatus.SENT && pingPongStatus == PingPongStatus.NONE
    }

    fun isExpiredAfterStopped(now: LocalDateTime): Boolean {
        return if (stoppedAt == null) {
            false
        } else {
            stoppedAt!!.plusDays(1L) <= now
        }
    }

    fun isNotExpiredAfterStopped(now: LocalDateTime): Boolean {
        return !isExpiredAfterStopped(now)
    }

    companion object {
        private const val DELETE_AFTER_DAYS = 3
    }
}
