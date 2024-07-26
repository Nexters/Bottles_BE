package com.nexters.bottles.auth.facade

import com.nexters.bottles.auth.component.WebClientAdapter
import com.nexters.bottles.user.service.UserService

class AuthFacade(
    private val userService: UserService,
    private val webClientAdapter: WebClientAdapter,
) {

    fun signInUp(code: String) {
        val userInfoResponse = webClientAdapter.sendAuthRequest(code)

    }
}
