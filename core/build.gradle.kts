import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    maven
    // id("com.github.johnrengelman.shadow") version "6.1.0"
}

dependencies {
    implementation(project(":api"))
    compileOnly(Libs.paper)
    compileOnly(Libs.skedule)
    compileOnly(Libs.coroutinesCore)
    compileOnly(Libs.mythicMobs)
    compileOnly(Libs.easyRanking)
    compileOnly(Libs.fwEchelonApi)
    compileOnly(Libs.vault)
}

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

tasks.withType<Jar> {
    from(project(":api").sourceSets["main"].output)
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