package com.nexters.bottles.api.user.facade.dto

import com.nexters.bottles.app.user.domain.enum.AlimyType

data class AlimyOnOffRequest(
    val alimyType: AlimyType,
    val enabled: Boolean,
) {
}
