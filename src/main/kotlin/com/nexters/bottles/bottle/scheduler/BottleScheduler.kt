package com.nexters.bottles.bottle.scheduler

import com.nexters.bottles.bottle.repository.BottleMatchingRepository
import com.nexters.bottles.bottle.repository.dto.UsersCanBeMatchedDto
import com.nexters.bottles.bottle.service.BottleHistoryService
import com.nexters.bottles.bottle.service.BottleService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BottleScheduler(
    private val bottleMatchingRepository: BottleMatchingRepository,
    private val bottleService: BottleService,
    private val bottleHistoryService: BottleHistoryService,
) {

    @Scheduled(cron = "0 0 9,18 * * *")
    fun match() {
        /*
        [매칭 조건]
        - 인당 최대 5개 보틀까지 매칭할 수 있음
        - A <- B 와 B <- A는 다른 경우임 (A가 B를 이전에 거절했어도, B에게 A의 보틀이 전달될 수 있음)
        - 하루에 두 번의 랜덤 매칭을 해줌 (일단 오전 9시, 오후 6시)
        - '같은 지역'에 사는 사람 우선으로 매칭 
         */

        val userIdsForMatching = bottleMatchingRepository.findAllUserIdForMatching(MAX_BOTTLE_COUNT)
        val usersCanBeMatched = bottleMatchingRepository.findAllUserCanBeMatched(userIdsForMatching)
        val groupByTargetUserId = usersCanBeMatched.shuffled()
            .groupBy { it.targetUserId }

        for (targetUserId in groupByTargetUserId.keys) {
            val usersCanBeMatchedWithTargetUser = groupByTargetUserId[targetUserId]
            if (usersCanBeMatchedWithTargetUser != null) {
                val matching = findUserSameRegionOrRandom(usersCanBeMatchedWithTargetUser)
                bottleService.saveBottle(matching.targetUserId, matching.willMatchUserId)
                bottleHistoryService.saveBottleHistory(matching.targetUserId, matching.willMatchUserId)
            }
        }
    }

    private fun findUserSameRegionOrRandom(usersCanBeMatchedDtos: List<UsersCanBeMatchedDto>): UsersCanBeMatchedDto {
        return usersCanBeMatchedDtos.firstOrNull {
            it.targetUserGender != it.willMatchUserGender
            it.targetUserProfileSelect.region.city == it.willMatchUserProfileSelect.region.city
        } ?: usersCanBeMatchedDtos[0]
    }

    companion object {
        private const val MAX_BOTTLE_COUNT = 5
    }
}
