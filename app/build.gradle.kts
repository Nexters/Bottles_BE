dependencies {
    api("org.springframework.boot:spring-boot-starter-web")

    api("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    api("io.github.microutils:kotlin-logging:3.0.5")

    api("org.jetbrains.kotlin:kotlin-reflect")

    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-data-jdbc")

    api("net.logstash.logback:logstash-logback-encoder:7.3")

    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

    implementation("com.google.firebase:firebase-admin:9.2.0")

    implementation("com.github.maricn:logback-slack-appender:1.3.0")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")

    val isMacOS: Boolean = System.getProperty("os.name").startsWith("Mac OS X")
    val architecture = System.getProperty("os.arch").toLowerCase()
    if (isMacOS && architecture == "aarch64") {
        implementation("io.netty:netty-resolver-dns-native-macos:4.1.75.Final") {
            artifact {
                classifier = "osx-aarch_64"
            }
        }
    }
}

tasks.named("jar") {
    enabled = true
}

tasks.named("bootJar") {
    enabled = false
}
