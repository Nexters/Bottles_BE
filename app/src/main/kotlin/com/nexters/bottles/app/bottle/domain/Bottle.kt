package com.nexters.bottles.app.bottle.domain

import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.common.BaseEntity
import com.nexters.bottles.app.user.domain.User
import java.time.LocalDateTime
import javax.persistence.Column
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    var targetUser: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_user_id")
    var sourceUser: User,

    var likeMessage: String? = null,

    @Column
    val expiredAt: LocalDateTime = LocalDateTime.now().plusDays(1),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stopped_user_id")
    var stoppedUser: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_select_user_id")
    var firstSelectUser: User? = null,

    @Column
    @Enumerated(value = EnumType.STRING)
    var bottleStatus: BottleStatus = BottleStatus.RANDOM,

    @Column
    @Enumerated(value = EnumType.STRING)
    var pingPongStatus: PingPongStatus = PingPongStatus.NONE,
) : BaseEntity() {

    fun sendLikeMessage(from: User, to: User, likeMessage: String) {
        this.targetUser = to
        this.sourceUser = from
        this.likeMessage = likeMessage
        this.bottleStatus = BottleStatus.SENT
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

    fun stop(stoppedBy: User) {
        this.stoppedUser = stoppedBy
        this.pingPongStatus = PingPongStatus.STOPPED
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
}
