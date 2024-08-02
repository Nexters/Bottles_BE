package com.nexters.bottles.auth.component

import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class AuthCodeGenerator {

    fun createRandomNumbers() : String {
        return Random.nextInt(100000, 1000000).toString()
    }
}
