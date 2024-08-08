package com.nexters.bottles.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication(
    scanBasePackages = [
        "com.nexters.bottles.app"
    ]
)
class BottlesBatchApplication

fun main(args: Array<String>) {
    runApplication<BottlesBatchApplication>(*args)
}
