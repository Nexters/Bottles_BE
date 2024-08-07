package com.nexters.bottles

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BottlesBatchApplication

fun main(args: Array<String>) {
    runApplication<BottlesBatchApplication>(*args)
}
