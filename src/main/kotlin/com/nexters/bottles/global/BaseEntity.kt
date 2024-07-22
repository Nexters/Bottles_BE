package com.nexters.bottles.global

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class BaseEntity(
    @CreationTimestamp
    open val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    open var updatedAt: LocalDateTime = LocalDateTime.now()
)
