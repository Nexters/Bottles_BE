package com.nexters.bottles.bottle.domain

import com.nexters.bottles.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.global.BaseEntity
import com.nexters.bottles.user.domain.User
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
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
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    val targetUser: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_user_id")
    val sourceUser: User,

    @Column
    val expiredAt: LocalDateTime = LocalDateTime.now().plusDays(1),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stopped_user_id")
    var stoppedUser: User? = null,

    @Column
    val pingPongStatus: PingPongStatus = PingPongStatus.NONE,
) : BaseEntity() {
}
