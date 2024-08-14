package com.nexters.bottles.app.bottle.repository

import com.nexters.bottles.app.bottle.repository.dto.UsersCanBeMatchedDto
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class BottleMatchingRepository(
    private val jdbcTemplate: JdbcTemplate,
) {

    /*
    - 보틀의 상태가 RANDOM
    - 보틀의 핑퐁 상태가 NONE
    - user profile의 introduction과 profile_select가 NULL이 아닌
    보틀의 수가 'MaxBottleCount' 개 미만인 user id 조회
     */
    fun findAllUserIdForMatching(maxBottleCount: Int): List<Long> {
        val sql = """
            SELECT u.id AS id 
            FROM user u 
            JOIN user_profile up ON up.user_id = u.id AND up.introduction IS NOT NULL AND up.profile_select IS NOT NULL 
            JOIN bottle b ON b.source_user_id = u.id AND b.bottle_statsu = 'RANDOM' AND b.ping_pong_status = 'NONE' 
            GROUP BY u.id 
            HAVING COUNT(u.id) < ?; 
        """.trimIndent()

        return jdbcTemplate.query(sql, { rs, _ ->
            rs.getLong("id")
        }, maxBottleCount)
    }

    /*
    각 user의 bottle_history에 matched_user_id가 아닌 user 조회
     */
    fun findAllUserCanBeMatched(userId: Long): List<UsersCanBeMatchedDto> {
        val sql = """
           SELECT u.id AS willMatchUserId, u.gender AS willMatchUserGender, u.city AS city
           FROM user u 
           LEFT JOIN bottle_history bh ON u.id = bh.matched_user_id 
           WHERE bh.matched_user_id IS NULL AND u.id != ? AND u.deleted = false AND u.is_match_activated = true;
       """.trimIndent()

        return jdbcTemplate.query(sql, { rs, _ ->
            UsersCanBeMatchedDto(
                willMatchUserId = rs.getLong("willMatchUserId"),
                willMatchUserGender = rs.getString("willMatchUserGender"),
                willMatchCity = rs.getString("city"),
            )
        }, userId)
    }
}
