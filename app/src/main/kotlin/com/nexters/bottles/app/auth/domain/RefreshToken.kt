package com.nexters.bottles.app.auth.domain

import com.nexters.bottles.app.common.BaseEntity
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val userId: Long,

    var token: String,

    val expiryDate: LocalDateTime,
) : BaseEntity()
