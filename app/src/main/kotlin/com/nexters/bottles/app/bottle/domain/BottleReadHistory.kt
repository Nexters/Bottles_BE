package com.nexters.bottles.app.bottle.domain

import com.nexters.bottles.app.common.BaseEntity
import com.nexters.bottles.app.user.domain.User
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class BottleReadHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bottle_id")
    val bottle: Bottle,

    var isReadByUser: Boolean = false
) : BaseEntity() {

    fun markRead() {
        isReadByUser = true
    }
}
