package com.nexters.bottles.api.user.facade.dto

data class BlockContactListRequest(
    val blockContacts: Set<String> = setOf<String>(),
) {
}
