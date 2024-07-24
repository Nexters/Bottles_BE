package com.nexters.bottles

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class BottlesApplication

fun main(args: Array<String>) {
    runApplication<BottlesApplication>(*args)
}
