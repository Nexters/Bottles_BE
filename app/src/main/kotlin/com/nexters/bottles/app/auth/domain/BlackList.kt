package com.nexters.bottles.app.auth.domain

import com.nexters.bottles.app.common.BaseEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class BlackList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val expiredAccessToken: String,
) : BaseEntity()
