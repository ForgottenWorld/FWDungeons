import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":api"))
}

plugins {
    maven
    kotlin("jvm") version Versions.kotlin
}

subprojects {

    group = "it.forgottenworld.fwdungeons"
    version = Versions.plugin

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
        maven("https://rayzr.dev/repo/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://mvn.lumine.io/repository/maven-public/")
        maven("https://jitpack.io")
    }

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

}