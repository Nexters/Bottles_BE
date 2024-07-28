package com.nexters.bottles.bottle.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.nexters.bottles.bottle.repository.dto.UserProfileSelectDto
import com.nexters.bottles.bottle.repository.dto.UsersCanBeMatchedDto
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class BottleMatchingRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val objectMapper: ObjectMapper
) {

    /*
    - 보틀의 상태가 NONE
    - user profile의 introduction과 profile_select가 NULL이 아닌
    보틀의 수가 'MaxBottleCount' 개 미만인 user id 조회
     */
    fun findAllUserIdForMatching(maxBottleCount: Int): List<Long> {
        val sql = "SELECT u.id AS id " +
                "FROM user u " +
                "JOIN user_profile up ON up.user_id = u.id AND up.introduction IS NOT NULL AND up.profile_select IS NOT NULL " +
                "JOIN bottle b ON b.source_user_id = u.id AND b.ping_pong_status = 'NONE' " +
                "GROUP BY u.id " +
                "HAVING COUNT(u.id) < ?;"

        return jdbcTemplate.query(sql, { rs, _ ->
            rs.getLong("id")
        }, maxBottleCount)
    }

    /*
    각 user의 bottle_history에 matched_user_id가 아닌 user 조회
     */
    fun findAllUserCanBeMatched(userIds: List<Long>): List<UsersCanBeMatchedDto> {
        val sql =
            "SELECT u1.id AS targetUserId, u1.gender AS targetUserGender, up1.profile_select AS targetUserProfileSelect, " +
                    "u2.id AS willMatchUserId, u2.gender AS willMatchUserGender, up2.profile_select AS willMatchUserProfileSelect " +
                    "FROM user u1 " +
                    "CROSS JOIN user u2 " +
                    "LEFT JOIN user_profile up1 ON u1.id = up1.user_id " +
                    "LEFT JOIN user_profile up2 ON u2.id = up.user_id " +
                    "LEFT JOIN bottle_history bh ON u1.id = bh.user_id AND u2.id = bh.matched_user_id " +
                    "WHERE bh.matched_user_id IS NULL AND u1.id != u2.id AND IN (?);"

        return jdbcTemplate.query(sql, { rs, _ ->
            UsersCanBeMatchedDto(
                targetUserId = rs.getLong("targetUserId"),
                targetUserGender = rs.getString("targetUserGender"),
                targetUserProfileSelect = objectMapper.readValue(
                    rs.getString("targetUserProfileSelect"),
                    UserProfileSelectDto::class.java
                ),
                willMatchUserId = rs.getLong("willMatchUserId"),
                willMatchUserGender = rs.getString("willMatchUserGender"),
                willMatchUserProfileSelect = objectMapper.readValue(
                    rs.getString("willMatchUserProfileSelect"),
                    UserProfileSelectDto::class.java
                )
            )
        }, userIds)
    }
}
