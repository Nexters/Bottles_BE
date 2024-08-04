package com.nexters.bottles.bottle.domain

import com.nexters.bottles.global.BaseEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class BottleHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val userId: Long,

    // 한 번 매칭된 적 있는 user id
    val matchedUserId: Long? = null,

    // 내가 거절한 적 있는 user id
    val refusedUserId: Long? = null
) : BaseEntity()
