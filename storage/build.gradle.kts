plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(project(":api"))
    compileOnly(Libs.paper)
}
