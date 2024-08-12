dependencies {
    api("org.springframework.boot:spring-boot-starter-web")

    api("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    api("io.github.microutils:kotlin-logging:3.0.5")

    api("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")

    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

    implementation("com.google.firebase:firebase-admin:9.2.0")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")
}

tasks.named("jar") {
    enabled = true
}

tasks.named("bootJar") {
    enabled = false
}
