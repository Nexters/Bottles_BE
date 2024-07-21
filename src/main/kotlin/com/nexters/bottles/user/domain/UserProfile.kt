package com.nexters.bottles.user.domain

import com.nexters.bottles.user.controller.dto.InterestDto
import com.nexters.bottles.user.controller.dto.RegionDto
import com.nexters.bottles.user.repository.converter.UserProfileSelectConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @Column(name = "profile_select")
    @Convert(converter = UserProfileSelectConverter::class)
    var profileSelect: UserProfileSelect,

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

data class UserProfileSelect(
    val mbti: String,
    val keyword: List<String> = arrayListOf(),
    val interest: InterestDto,
    val job: String,
    val smoking: String,
    val alcohol: String,
    val religion: String,
    val region: RegionDto,
)

