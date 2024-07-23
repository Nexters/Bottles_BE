package com.nexters.bottles.user.service

import com.nexters.bottles.user.domain.User
import com.nexters.bottles.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {
}
