dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    implementation("com.google.firebase:firebase-admin:9.2.0")

    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")
}

tasks.named("jar") {
    enabled = true
}

tasks.named("bootJar") {
    enabled = false
}
