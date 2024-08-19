package com.nexters.bottles.app.user.domain

import com.nexters.bottles.app.common.BaseEntity
import com.nexters.bottles.app.user.domain.enum.Gender
import com.nexters.bottles.app.user.domain.enum.SignUpType
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var birthdate: LocalDate,

    var name: String,

    var city: String? = null,  // 시
    var state: String? = null, // 구

    var kakaoId: String? = null,

    var phoneNumber: String? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = [CascadeType.ALL])
    var userProfile: UserProfile? = null,

    @Enumerated(EnumType.STRING)
    var gender: Gender = Gender.MALE,

    @Enumerated(EnumType.STRING)
    var signUpType: SignUpType = SignUpType.NORMAL,

    var deleted: Boolean = false,

    var deletedAt: LocalDateTime? = null,

    var lastActivatedAt: LocalDateTime = LocalDateTime.now(),

    var isMatchActivated: Boolean = true,
) : BaseEntity() {

    fun getKoreanAge(): Int {
        return LocalDate.now().year - birthdate.year + 1
    }

    fun softDelete() {
        this.deleted = true
        this.deletedAt = LocalDateTime.now()
    }

    fun updateLastActivatedAt(basedAt: LocalDateTime) {
        this.lastActivatedAt = basedAt
    }

    fun isMatchInactive(): Boolean {
        return !isMatchActivated
    }

    fun isNotRegisterProfile(): Boolean {
        return userProfile?.isNotRegisterIntroductionOrImage() ?: false
    }
}
