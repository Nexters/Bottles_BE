package com.nexters.bottles.profile.domain

import com.nexters.bottles.profile.repository.converter.ProfileSelectConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id")
    var userId: Long,

    @Column(name = "profile_select")
    @Convert(converter = ProfileSelectConverter::class)
    var profileSelect: ProfileSelect,

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
