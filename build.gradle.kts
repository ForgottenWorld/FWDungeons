import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.4.21"
    java
    kotlin("jvm") version kotlinVersion
    maven
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://rayzr.dev/repo/")
}

dependencies {
    compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:0.0.6")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
    compileOnly("com.destroystokyo.paper:paper-api:1.16.4-R0.1-SNAPSHOT")
    compileOnly("io.lumine.xikage:MythicMobs:4.10.0")
    compileOnly("com.github.ForgottenWorld:EasyRanking:7362fe6420")
}

group = "it.forgottenworld"
version = "0.11.3"
description = "fwdungeons"
java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.register("localDeploy") {
    doLast {
        copy {
            from("build/libs")
            into("/home/giacomo/paper/plugins")
            include("**/*.jar")
        }
    }
}