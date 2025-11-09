plugins {
    kotlin("jvm") version "2.2.20"
    `maven-publish`
}

base {
    archivesName.set("sunset-config")
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
        maven("https://libraries.minecraft.net")
    }

    dependencies {
    }

    group = "xyz.bluspring.sunset"
    version = "1.2.0"

    java {
        withSourcesJar()
    }

    project.extensions.configure<PublishingExtension>("publishing") {
        repositories {
            maven("https://mvn.devos.one/releases") {
                credentials {
                    username = System.getenv()["MAVEN_USER"]
                    password = System.getenv()["MAVEN_PASS"]
                }
            }
        }

        publications {
            create<MavenPublication>("mavenJava") {
                groupId = project.group.toString()
                artifactId = project.base.archivesName.get()
                version = project.version.toString()

                from(components["java"])
            }
        }
    }
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
