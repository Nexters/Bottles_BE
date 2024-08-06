package com.nexters.bottles.api.bottle.domain

import com.nexters.bottles.api.bottle.domain.enum.BottleStatus
import com.nexters.bottles.api.global.BaseEntity
import com.nexters.bottles.api.user.domain.User
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

    var targetUserSelect: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_user_id")
    var sourceUser: User,

    var sourceUserSelect: Boolean = false,

    var likeMessage: String? = null,

    @Column
    val expiredAt: LocalDateTime = LocalDateTime.now().plusDays(1),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stopped_user_id")
    var stoppedUser: User? = null,

    @Column
    @Enumerated(value = EnumType.STRING)
    var bottleStatus: BottleStatus = BottleStatus.RANDOM,

    @Column
    @Enumerated(value = EnumType.STRING)
    var pingPongStatus: com.nexters.bottles.api.bottle.domain.enum.PingPongStatus = com.nexters.bottles.api.bottle.domain.enum.PingPongStatus.NONE,
) : BaseEntity() {

    fun sendLikeMessage(from: User, to: User, likeMessage: String) {
        this.targetUser = to
        this.sourceUser = from
        this.likeMessage = likeMessage
        this.bottleStatus = BottleStatus.SENT
    }

    fun startPingPong() {
        this.pingPongStatus = com.nexters.bottles.api.bottle.domain.enum.PingPongStatus.ACTIVE
    }

    fun refuse(refusedBy: User) {
        this.stoppedUser = refusedBy
        this.pingPongStatus = com.nexters.bottles.api.bottle.domain.enum.PingPongStatus.REFUSED
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
        this.pingPongStatus = com.nexters.bottles.api.bottle.domain.enum.PingPongStatus.STOPPED
    }

    fun selectMatch(userId: Long, willMatch: Boolean) {
        when (userId) {
            targetUser.id -> targetUserSelect = willMatch
            sourceUser.id -> sourceUserSelect = willMatch
            else -> throw IllegalArgumentException("고객센터에 문의해주세요")
        }
        if (targetUserSelect && sourceUserSelect) {
            pingPongStatus = com.nexters.bottles.api.bottle.domain.enum.PingPongStatus.MATCHED
        }
    }
}
