package com.nexters.bottles.app.auth.domain

import com.nexters.bottles.app.auth.domain.enum.TokenType
import com.nexters.bottles.app.common.BaseEntity
import javax.persistence.*

@Entity
class BlackList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    val tokenType: TokenType = TokenType.ACCESS_TOKEN,

    val expiredAccessToken: String, // refreshToken도 저장할 수 있게 변경되었으나 과거에 작성한 변수명이라 그냥 둠
) : BaseEntity()
