package com.nexters.bottles.api.auth.facade.dto

data class AppleIdTokenPayload(
    val iss: String,
    val sub: String,
    val aud: String,
    val exp: String,
    val iat: String,
    val c_hash: String,
    val auth_time: String,
    val nonce_supported: String,
) {

}
