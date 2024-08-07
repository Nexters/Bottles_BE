package com.nexters.bottles.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication(
    scanBasePackages = [
        "com.nexters.bottles.api",
        "com.nexters.bottles.app",
    ]
)
class BottlesApiApplication

fun main(args: Array<String>) {
    runApplication<BottlesApiApplication>(*args)
}
