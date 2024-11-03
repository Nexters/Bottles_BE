package com.nexters.bottles.app.bottle.domain

import com.nexters.bottles.app.common.BaseEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class BottleReadHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
    val userId: Long,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "bottle_id")
    val bottleId: Long,

    val isReadByUser: Boolean = false
) : BaseEntity()
