package com.nexters.bottles.auth.domain

import com.nexters.bottles.global.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "auth_sms")
class AuthSms(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val phoneNumber: String,

    val authCode: String,

    val expiredAt: LocalDateTime,
) : BaseEntity() {

    fun validate(authCode: String) {
        if (this.authCode != authCode) {
            throw IllegalArgumentException("다시 확인 해주세요")
        }
    }
}
