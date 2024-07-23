package com.nexters.bottles.bottle.service

import com.nexters.bottles.bottle.domain.Bottle
import com.nexters.bottles.bottle.repository.BottleRepository
import com.nexters.bottles.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BottleService(
    private val bottleRepository: BottleRepository,
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getBottles(): List<Bottle> {
        // TODO User 회원 가입 기능 구현후 수정
        val user = userRepository.findByIdOrNull(1L) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        return bottleRepository.findByTargetUserAndNotExpired(user, LocalDateTime.now())
    }

    @Transactional(readOnly = true)
    fun getBottle(bottleId: Long): Bottle {
        return bottleRepository.findByIdAndNotExpired(bottleId, LocalDateTime.now())
            ?: throw IllegalArgumentException("이미 떠내려간 보틀이에요")
    }
}
