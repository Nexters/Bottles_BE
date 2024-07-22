package com.nexters.bottles.bottle.domain

import com.nexters.bottles.global.BaseEntity
import com.nexters.bottles.user.domain.User
import java.time.LocalDateTime
import javax.persistence.*

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
    val expiredAt: LocalDateTime = LocalDateTime.now().plusDays(1)
) : BaseEntity() {
}
