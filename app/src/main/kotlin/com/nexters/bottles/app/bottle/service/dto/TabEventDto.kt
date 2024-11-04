package com.nexters.bottles.app.bottle.service.dto

import com.nexters.bottles.app.bottle.domain.enum.TabType

data class TabEventDto(
    val tabType: TabType,
    val isNewBadgeVisible: Boolean
) {
}
