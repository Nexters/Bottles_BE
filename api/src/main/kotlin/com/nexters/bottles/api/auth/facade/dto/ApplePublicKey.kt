package com.nexters.bottles.api.auth.facade.dto

data class ApplePublicKeys(
    val keys: List<ApplePublicKey>
)

data class ApplePublicKey(
    val kty: String? = "RSA",
    val kid: String,
    val alg: String,
    val n: String,
    val e: String,
    val use: String,
)
