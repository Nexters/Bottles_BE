package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.Letter
import com.nexters.bottles.app.bottle.domain.LetterQuestionAndAnswer
import com.nexters.bottles.app.bottle.domain.Question
import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.bottle.repository.BottleMatchingRepository
import com.nexters.bottles.app.bottle.repository.BottleReadHistoryRepository
import com.nexters.bottles.app.bottle.repository.BottleRepository
import com.nexters.bottles.app.bottle.repository.LetterRepository
import com.nexters.bottles.app.bottle.repository.dto.UsersCanBeMatchedDto
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class BottleService(
    private val bottleRepository: BottleRepository,
    private val userRepository: UserRepository,
    private val letterRepository: LetterRepository,
    private val bottleMatchingRepository: BottleMatchingRepository,
    private val bottleReadHistoryRepository: BottleReadHistoryRepository,
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
    fun getNewBottlesByBottleStatus(user: User, statusSet: Set<BottleStatus>): List<Bottle> {
        return bottleRepository.findAllByTargetUserAndStatusesAndNotExpiredAndDeletedFalseOrderByCreatedDesc(
            user,
            statusSet,
            setOf(PingPongStatus.NONE),
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
    fun acceptBottle(userId: Long, bottleId: Long, likeMessage: String?, questions: List<Question>): Bottle {
        val bottle =
            bottleRepository.findByIdAndStatusAndNotExpiredAndDeletedFalse(
                bottleId,
                setOf(PingPongStatus.NONE),
                LocalDateTime.now()
            ) ?: throw IllegalArgumentException("이미 떠내려간 보틀이에요")

        when (bottle.bottleStatus) {
            BottleStatus.RANDOM -> {
                requireNotNull(likeMessage) { "고객센터에 문의해주세요" }
                bottle.sendLikeMessage(
                    from = bottle.targetUser,
                    to = bottle.sourceUser,
                    likeMessage = likeMessage,
                    LocalDateTime.now()
                )
            }

            BottleStatus.SENT -> {
                require(likeMessage == null) { "고객센터에 문의해주세요" }
                bottle.startPingPong()

                val letters = findRandomQuestions(questions)
                saveLetter(bottle, bottle.targetUser, letters)
                saveLetter(bottle, bottle.sourceUser, letters)
            }
        }
        return bottle
    }

    private fun findRandomQuestions(questions: List<Question>) = questions
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

        val refusedUser =
            userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        bottle.refuse(refusedUser)
        return bottle
    }

    @Transactional
    fun stop(userId: Long, bottleId: Long): Bottle {
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
        return bottle
    }

    @Transactional(readOnly = true)
    fun getPingPongBottles(userId: Long): List<Bottle> {
        val user = userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
        return bottleRepository.findAllByNotDeletedUserAndStatusAndDeletedFalseOrderByUpdatedAtDesc(
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
    fun matchRandomBottle(
        userId: Long,
        matchingHour: Int,
        blockUserIds: Set<Long>,
        blockedMeUserIds: Set<Long>,
        count: Int = 1,
    ): List<Bottle> {
        val user = userRepository.findByIdOrNull(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        if (user.isNotRegisterProfile()) return emptyList()
        if (user.isMatchInactive()) return emptyList()

        val matchingTime = getMatchingTime(matchingHour)
        if (user.lastRandomMatchedAt > matchingTime) return emptyList()

        val usersCanBeMatched = bottleMatchingRepository.findAllUserCanBeMatched(user.id, user.gender!!)
            .filter { it.willMatchUserId !in blockUserIds }
            .filter { it.willMatchUserId !in blockedMeUserIds }

        if (usersCanBeMatched.isEmpty()) return emptyList()

        val matchingUserDtos = findUserSameRegionOrRandom(usersCanBeMatched, user, count)
        val matchingUsers = userRepository.findByIdInAndDeletedFalse(matchingUserDtos.map { it.willMatchUserId })

        val now = LocalDateTime.now()
        var bottles = matchingUsers.map { matchingUser -> Bottle(targetUser = user, sourceUser = matchingUser, expiredAt = now.plusDays(1)) }
        val savedBottles = bottleRepository.saveAll(bottles)

        user.updateLastRandomMatchedAt(LocalDateTime.now())

        return savedBottles
    }

    @Transactional
    fun matchAdditionalRandomBottle(
        userId: Long,
        matchingHour: Int,
        blockUserIds: Set<Long>,
        blockedMeUserIds: Set<Long>,
        count: Int = 1,
    ): List<Bottle> {
        val user = userRepository.findByIdOrNull(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        if (user.isNotRegisterProfile()) return emptyList()
        if (user.isMatchInactive()) return emptyList()

        var usersCanBeMatched = bottleMatchingRepository.findAllUserCanBeMatched(user.id, user.gender!!)
            .filter { it.willMatchUserId !in blockUserIds }
            .filter { it.willMatchUserId !in blockedMeUserIds }

        // 보틀 더받기 API는 매칭 상대가 없으면 이전에 매칭된 상대라도 보여준다
        if (usersCanBeMatched.isEmpty()) {
            usersCanBeMatched = bottleMatchingRepository.findAdditionalAllUserCanBeMatched(user.id, user.gender!!)
                .filter { it.willMatchUserId !in blockUserIds }
                .filter { it.willMatchUserId !in blockedMeUserIds }
        }

        if (usersCanBeMatched.isEmpty()) return emptyList()

        val matchingUserDtos = findUserSameRegionOrRandom(usersCanBeMatched, user, count)
        val matchingUsers = userRepository.findByIdInAndDeletedFalse(matchingUserDtos.map { it.willMatchUserId })

        val now = LocalDateTime.now()
        var bottles = matchingUsers.map { matchingUser -> Bottle(targetUser = user, sourceUser = matchingUser, expiredAt = now.plusDays(1)) }
        val savedBottles = bottleRepository.saveAll(bottles)

        user.updateLastRandomMatchedAt(LocalDateTime.now())

        return savedBottles
    }

    private fun getMatchingTime(matchingHour: Int): LocalDateTime {
        val now = LocalDateTime.now()
        var matchingTime = now.with(LocalTime.of(matchingHour, 0))
        if (now.hour < matchingHour) {
            matchingTime = matchingTime.minusDays(1)
        }
        return matchingTime
    }

    private fun findUserSameRegionOrRandom(
        usersCanBeMatchedDtos: List<UsersCanBeMatchedDto>,
        targetUser: User,
        count: Int,
    ): List<UsersCanBeMatchedDto> {
        val canBeMatchedDtos = usersCanBeMatchedDtos.shuffled()
            .filter {
                targetUser.gender?.name != it.willMatchUserGender
                targetUser.city == it.willMatchCity
            }


        // 필터링된 사용자가 count에 도달하지 못하면 추가로 사용자 채우기
        return if (canBeMatchedDtos.size < count) {
            val additionalDtos = usersCanBeMatchedDtos.shuffled()
                .filter { it !in canBeMatchedDtos } // 이미 선택된 항목을 제외
                .take(count - canBeMatchedDtos.size) // 모자란 개수만큼 추가
            canBeMatchedDtos + additionalDtos // 기존 결과에 추가 결과를 합쳐서 반환
        } else {
            canBeMatchedDtos
        }
    }

    @Transactional(readOnly = true)
    fun findBottleById(bottleId: Long): Bottle {
        return bottleRepository.findByIdOrNull(bottleId) ?: throw IllegalArgumentException("존재하지 않는 보틀입니다")
    }

    @Transactional(readOnly = true)
    fun getPingPongBottlesByDeletedUser(userId: Long): List<Bottle> {
        val user = userRepository.findByIdOrNull(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
        return bottleRepository.findAllByUserAndStatusAndDeletedFalse(
            user,
            setOf(
                PingPongStatus.ACTIVE,
                PingPongStatus.STOPPED,
                PingPongStatus.MATCHED,
            )
        )
    }

    @Transactional
    fun stopPingPongBottlesByDeletedUser(userId: Long) {
        val user = userRepository.findByIdOrNull(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
        val activeBottles = bottleRepository.findAllByUserAndStatusAndDeletedFalse(
            user,
            setOf(
                PingPongStatus.ACTIVE,
            )
        )
        activeBottles.forEach {
            it.stop(user, LocalDateTime.now())
        }
    }

    @Transactional
    fun matchFirstRandomBottle(userId: Long, count: Int): List<Bottle> {
        val user = userRepository.findByIdOrNull(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        val usersCanBeMatched = bottleMatchingRepository.findAllUserCanBeMatchedWithoutIntroduction(user.id, user.gender!!).take(count)
        if (usersCanBeMatched.isEmpty()) return emptyList()

        val matchingUserDtos = findUserSameRegionOrRandom(usersCanBeMatched, user, count)
        val matchingUsers = userRepository.findByIdInAndDeletedFalse(matchingUserDtos.map { it.willMatchUserId })

        val now = LocalDateTime.now()
        var bottles = matchingUsers.map { matchingUser -> Bottle(targetUser = user, sourceUser = matchingUser, expiredAt = now.plusDays(1)) }
        val savedBottles = bottleRepository.saveAll(bottles)

        user.updateLastRandomMatchedAt(now)

        return savedBottles
    }

    // TODO 클라이언트에서 문답 읽음 표시를 v2로 옮긴 후 변경 -> Letter의 isReadByOtherUser 제거 (이후 읽음 표시는 BottleReadHistory 한곳에서만 관리하도록 함)
    @Transactional(readOnly = true)
    fun isAllReadPingPongBottles(userId: Long): Boolean {
        val user = userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
        val userLetters = letterRepository.findAllByPingPongStatus(
            setOf(
                PingPongStatus.ACTIVE,
                PingPongStatus.MATCHED,
                PingPongStatus.STOPPED
            )
        )
        val unreadLetters = userLetters.filter { it.user.id != user.id }
            .filter { !it.isReadByOtherUser }
        return unreadLetters.isEmpty()
    }

    @Transactional(readOnly = true)
    fun isAllReadByBottleStatus(userId: Long, bottleStatus: BottleStatus): Boolean {
        val user = userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
        val bottleReadHistories = bottleReadHistoryRepository.findAllByUserAndBottleStatus(user, bottleStatus)
        val unreadBottles = bottleReadHistories.filter { !it.isReadByUser }
        return unreadBottles.isEmpty()
    }
}
