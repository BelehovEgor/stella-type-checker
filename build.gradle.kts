import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.0"
    application
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.ebelekhov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.antlr:antlr4-runtime:4.13.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("dev.ebelekhov.typechecker.MainKt")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveFileName.set("stella-type-checker.jar")
        mergeServiceFiles()
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}