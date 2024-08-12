package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.Letter
import com.nexters.bottles.app.bottle.domain.LetterQuestionAndAnswer
import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.bottle.repository.BottleMatchingRepository
import com.nexters.bottles.app.bottle.repository.BottleRepository
import com.nexters.bottles.app.bottle.repository.LetterRepository
import com.nexters.bottles.app.bottle.repository.QuestionRepository
import com.nexters.bottles.app.bottle.repository.dto.UsersCanBeMatchedDto
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BottleService(
    private val bottleRepository: BottleRepository,
    private val userRepository: UserRepository,
    private val letterRepository: LetterRepository,
    private val questionRepository: QuestionRepository,
    private val bottleMatchingRepository: BottleMatchingRepository,
) {

    @Transactional(readOnly = true)
    fun getNewBottles(user: User): List<Bottle> {
        return bottleRepository.findAllByTargetUserAndStatusAndNotExpiredAndDeletedFalse(
            user,
            PingPongStatus.NONE,
            LocalDateTime.now()
        )
    }

    @Transactional(readOnly = true)
    fun getNotExpiredBottle(
        bottleId: Long,
        statusSet: Set<PingPongStatus>
    ): Bottle {
        return bottleRepository.findByIdAndStatusAndNotExpiredAndDeletedFalse(bottleId, statusSet, LocalDateTime.now())
            ?: throw IllegalArgumentException("이미 떠내려간 보틀이에요")
    }

    @Transactional
    fun acceptBottle(userId: Long, bottleId: Long, likeMessage: String?) {
        val bottle =
            bottleRepository.findByIdAndStatusAndNotExpiredAndDeletedFalse(
                bottleId,
                setOf(PingPongStatus.NONE),
                LocalDateTime.now()
            ) ?: throw IllegalArgumentException("이미 떠내려간 보틀이에요")

        val targetUser =
            userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
        val sourceUser = userRepository.findByIdAndDeletedFalse(bottle.sourceUser.id)
            ?: throw IllegalArgumentException("탈퇴한 회원이에요")

        when (bottle.bottleStatus) {
            BottleStatus.RANDOM -> {
                requireNotNull(likeMessage) { "고객센터에 문의해주세요" }
                bottle.sendLikeMessage(
                    from = targetUser,
                    to = sourceUser,
                    likeMessage = likeMessage,
                    LocalDateTime.now()
                )
            }

            BottleStatus.SENT -> {
                require(likeMessage == null) { "고객센터에 문의해주세요" }
                bottle.startPingPong()
                val letters = findRandomQuestions()
                saveLetter(bottle, targetUser, letters)
                saveLetter(bottle, sourceUser, letters)
            }
        }
    }

    private fun findRandomQuestions() = questionRepository.findAll()
        .shuffled()
        .take(3)
        .map {
            LetterQuestionAndAnswer(question = it.question)
        }

    private fun saveLetter(
        bottle: Bottle,
        user: User,
        letters: List<LetterQuestionAndAnswer>
    ) {
        val letter = Letter(bottle = bottle, user = user, letters = letters)
        letterRepository.save(letter)
    }

    @Transactional
    fun refuseBottle(userId: Long, bottleId: Long): Bottle {
        val bottle =
            bottleRepository.findByIdAndStatusAndNotExpiredAndDeletedFalse(
                bottleId,
                setOf(PingPongStatus.NONE),
                LocalDateTime.now()
            ) ?: throw IllegalArgumentException("이미 떠내려간 보틀이에요")

        val targetUser =
            userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
        userRepository.findByIdAndDeletedFalse(bottle.sourceUser.id) ?: throw IllegalArgumentException("탈퇴한 회원이에요")

        bottle.refuse(targetUser)
        return bottle
    }


    @Transactional
    fun stop(userId: Long, bottleId: Long) {
        val bottle = bottleRepository.findByIdAndStatusAndDeletedFalse(
            bottleId,
            setOf(
                PingPongStatus.ACTIVE,
                PingPongStatus.MATCHED,
                PingPongStatus.STOPPED
            )
        ) ?: throw IllegalArgumentException("고객센터에 문의해주세요")
        val stoppedUser =
            userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        bottle.stop(stoppedUser, LocalDateTime.now())
    }

    @Transactional(readOnly = true)
    fun getPingPongBottles(userId: Long): List<Bottle> {
        val user = userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        return bottleRepository.findAllByUserAndStatusAndDeletedFalse(
            user,
            setOf(
                PingPongStatus.ACTIVE,
                PingPongStatus.MATCHED,
                PingPongStatus.STOPPED
            )
        )
    }

    @Transactional(readOnly = true)
    fun getPingPongBottle(bottleId: Long): Bottle {
        return bottleRepository.findByIdAndStatusAndDeletedFalse(
            bottleId,
            setOf(
                PingPongStatus.ACTIVE,
                PingPongStatus.MATCHED,
                PingPongStatus.STOPPED
            )
        ) ?: throw IllegalArgumentException("고객센터에 문의해주세요")
    }

    @Transactional
    fun matchRandomBottle(user: User, matchingTime: LocalDateTime): Bottle? {
        val todayMatchingBottle = bottleRepository.findByTargetUserAndBottleStatusAndCreatedAtAfter(
            targetUser = user,
            bottleStatus = BottleStatus.RANDOM,
            matchingTime = matchingTime
        )
        if (todayMatchingBottle.isNotEmpty()) return null

        val usersCanBeMatched = bottleMatchingRepository.findAllUserCanBeMatched(user.id)
        if (usersCanBeMatched.isEmpty()) return null

        val matchingUserDto = findUserSameRegionOrRandom(usersCanBeMatched, user)
        val matchingUser = userRepository.findByIdAndDeletedFalse(matchingUserDto.willMatchUserId)
            ?: throw IllegalArgumentException("탈퇴한 회원입니다")

        val bottle = Bottle(targetUser = user, sourceUser = matchingUser)
        return bottleRepository.save(bottle)
    }

    private fun findUserSameRegionOrRandom(
        usersCanBeMatchedDtos: List<UsersCanBeMatchedDto>,
        targetUser: User
    ): UsersCanBeMatchedDto {
        return usersCanBeMatchedDtos.shuffled()
            .firstOrNull {
                targetUser.gender.name != it.willMatchUserGender
                targetUser.city == it.willMatchCity
            } ?: usersCanBeMatchedDtos[0]
    }
}
