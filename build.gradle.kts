import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.davidgomes"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation("dev.kord:kord-core:0.8.0-M17")

    implementation("com.sksamuel.hoplite:hoplite-core:2.7.0")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.7.0")

    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.slf4j:slf4j-simple:2.0.5")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}

tasks.shadowJar {
    archiveBaseName.set("HairCheckBot")
    archiveClassifier.set("")
    archiveVersion.set("")
}
