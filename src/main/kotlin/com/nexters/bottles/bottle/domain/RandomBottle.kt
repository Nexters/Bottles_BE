package com.nexters.bottles.bottle.domain

import com.nexters.bottles.bottle.domain.enum.RandomBottleStatus
import com.nexters.bottles.global.BaseEntity
import com.nexters.bottles.user.domain.User
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
class RandomBottle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    val targetUser: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_user_id")
    val sourceUser: User,

    @Column
    val expiredAt: LocalDateTime = LocalDateTime.now().plusDays(1),

    @Column
    @Enumerated(value = EnumType.STRING)
    var status: RandomBottleStatus = RandomBottleStatus.NONE,
) : BaseEntity()
