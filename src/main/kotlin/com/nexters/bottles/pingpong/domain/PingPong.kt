package com.nexters.bottles.pingpong.domain

import com.nexters.bottles.global.BaseEntity
import com.nexters.bottles.pingpong.domain.enum.PingPongStatus
import com.nexters.bottles.user.domain.User
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class PingPong(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_a_id")
    val userA: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_b_id")
    val userB: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stopped_user_id")
    var stoppedUser: User? = null,

    @Column
    val status: PingPongStatus = PingPongStatus.ACTIVE,
) : BaseEntity()
