package com.nexters.bottles.auth.domain

import com.nexters.bottles.global.BaseEntity
import java.time.LocalDateTime
import java.util.Date
import javax.persistence.*

@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val userId: Long,

    val token: String,

    val expiryDate: LocalDateTime,
) : BaseEntity()
