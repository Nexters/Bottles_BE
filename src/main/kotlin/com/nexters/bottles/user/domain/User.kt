package com.nexters.bottles.user.domain

import com.nexters.bottles.global.BaseEntity
import com.nexters.bottles.user.domain.enum.Gender
import java.time.LocalDate
import javax.persistence.*

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var birthdate: LocalDate? = null,

    var name: String? = null,

    var kakaoId: String? = null,

    var phoneNumber: String? = null,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL])
    var userProfile: UserProfile? = null,

    @Enumerated(EnumType.STRING)
    var gender: Gender = Gender.MALE,

) : BaseEntity() {
}
