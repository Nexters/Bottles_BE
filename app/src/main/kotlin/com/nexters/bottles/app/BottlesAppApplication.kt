package com.nexters.bottles.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication(
    scanBasePackages = [
        "com.nexters.bottles.app",
    ]
)
class BottlesAppApplication

fun main(args: Array<String>) {
    runApplication<BottlesAppApplication>(*args)
}
