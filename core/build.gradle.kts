import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    maven
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

dependencies {
    implementation(project(":api"))
    implementation(Libs.Guice.guice)
    implementation(Libs.Guice.assistedInject)
    compileOnly(Libs.paper)
    compileOnly(Libs.skedule)
    compileOnly(Libs.coroutinesCore)
    compileOnly(Libs.mythicMobs) {
        exclude("net.kyori.adventure.text")
    }
    compileOnly(Libs.easyRanking)
    compileOnly(Libs.fwEchelonApi)
    compileOnly(Libs.vault)
}

description = "fwdungeons"
java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks.withType<ShadowJar> {
    dependencies {
        val included = listOf(
            "it.forgottenworld.dungeons",
            "com.google.inject",
            "javax.inject",
            "aopalliance"
        )
        exclude { dep ->
            included.none { dep.moduleGroup.startsWith(it) }
        }
    }
    relocate("com.google.inject", "it.forgottenworld.dungeons.google.inject")
    relocate("javax.inject", "it.forgottenworld.dungeons.javax.inject")
    relocate("org.aopalliance", "it.forgottenworld.dungeons.org.aopalliance")
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

tasks.withType<Jar> {
    from(project(":api").sourceSets["main"].output)
}

tasks.register("localDeploy") {
    doLast {
        copy {
            from("build/libs")
            into(Config.LOCAL_PLUGINS_DIR)
            include("**/*-all.jar")
        }
    }
}