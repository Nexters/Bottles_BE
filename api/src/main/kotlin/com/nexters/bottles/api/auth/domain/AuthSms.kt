package com.nexters.bottles.api.auth.domain

import com.nexters.bottles.api.global.BaseEntity
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

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
