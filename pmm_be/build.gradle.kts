plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "salad.example"
version = "0.0.1-SNAPSHOT"
description = "pmm_be"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")   // ทางลัดแบบเร็ว
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") // (สำหรับ JPA ในขั้นต่อไป)
    runtimeOnly("org.postgresql:postgresql:42.7.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.security:spring-security-crypto") // BCrypt
}

tasks.withType<Test> {
    useJUnitPlatform()
}
