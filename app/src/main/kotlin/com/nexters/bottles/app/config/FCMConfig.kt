package com.nexters.bottles.app.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.nio.file.Files
import java.nio.file.Paths
import javax.annotation.PostConstruct


@Configuration
class FCMConfig(
    @Value("\${firebase.service-account-key-path}")
    val filePath: String
) {

    @PostConstruct
    fun init() {
        val path = Paths.get(filePath)
        val inputStream = Files.newInputStream(path)

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(inputStream))
            .build()

        FirebaseApp.initializeApp(options)
    }
}
