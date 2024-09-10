package com.nexters.bottles.app.user.domain

import com.nexters.bottles.app.common.BaseEntity
import com.nexters.bottles.app.user.domain.enum.AlimyType
import org.springframework.data.relational.core.mapping.Table
import javax.persistence.*

@Entity
data class UserAlimy(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val userId: Long,

    @Enumerated(EnumType.STRING)
    val alimyType: AlimyType,

    val enabled: Boolean = true,
): BaseEntity() {
}
