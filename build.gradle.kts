plugins {
    kotlin("jvm") version "1.4.30"
    id("org.jetbrains.dokka") version "1.4.20"
    `maven-publish`
}

group = "xyz.teogramm"
version = "0.9.0"

repositories {
    mavenCentral()
    jcenter()
}

tasks {
    jar {
        enabled = false
    }
}

sourceSets {
    create("debug") {
        compileClasspath += sourceSets.main.get().runtimeClasspath
        runtimeClasspath += sourceSets.main.get().runtimeClasspath
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}
