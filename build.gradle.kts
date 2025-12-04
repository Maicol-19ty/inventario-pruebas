plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("checkstyle")
    id("pmd")
    id("com.github.spotbugs") version "6.0.26"
}

group = "cue.edu.co"
version = "0.0.1-SNAPSHOT"
description = "inventario-pruebas"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Database
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Development
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.h2database:h2")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // E2E Testing
    testImplementation("org.seleniumhq.selenium:selenium-java:4.24.0")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.9.2")

    // Static Analysis
    checkstyle("com.puppycrawl.tools:checkstyle:10.18.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Task for running tests without E2E (useful when Chrome is not available)
tasks.register<Test>("testWithoutE2E") {
    useJUnitPlatform {
        excludeTags("e2e")
    }
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    filter {
        excludeTestsMatching("*E2ETest")
    }
}

// Checkstyle Configuration
checkstyle {
    toolVersion = "10.18.1"
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
}

// PMD Configuration
pmd {
    toolVersion = "7.7.0"
    isIgnoreFailures = false
    ruleSets = listOf()
    ruleSetFiles = files("${rootDir}/config/pmd/ruleset.xml")
}

// SpotBugs Configuration
spotbugs {
    ignoreFailures.set(false)
    effort.set(com.github.spotbugs.snom.Effort.MAX)
    reportLevel.set(com.github.spotbugs.snom.Confidence.LOW)
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
    reports.create("html") {
        required.set(true)
    }
}
