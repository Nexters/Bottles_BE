dependencies {
    implementation(project(":api"))

    implementation("org.springframework.boot:spring-boot-starter")
}

tasks.named("jar") {
    enabled = true
}

tasks.named("bootJar") {
    enabled = false
}
