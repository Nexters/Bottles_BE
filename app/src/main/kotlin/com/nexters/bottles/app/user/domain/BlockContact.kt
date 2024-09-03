package com.nexters.bottles.app.user.domain

import com.nexters.bottles.app.common.BaseEntity
import javax.persistence.*

@Entity
class BlockContact(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val userId: Long,   // 차단 등록한 유저

    var phoneNumber: String,
) : BaseEntity() {
}
