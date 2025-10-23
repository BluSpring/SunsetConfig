plugins {
    kotlin("jvm") version "2.2.20"
}

group = "xyz.bluspring"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
}

dependencies {
    api(kotlin("reflect"))
    testImplementation(kotlin("test"))
    api(libs.datafixerupper)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
