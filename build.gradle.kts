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
    version = "1.3.1"

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

val stubs by sourceSets.creating
val stubsSquared by sourceSets.creating
val dfu6 by sourceSets.creating
val dfu8 by sourceSets.creating

dependencies {
    api(kotlin("reflect"))
    testImplementation(kotlin("test"))
    compileOnly(libs.datafixerupper.v6)

    "stubsCompileOnly"(libs.datafixerupper.v6)
    "stubsCompileOnly"(stubsSquared.output)
    "dfu6Api"(libs.datafixerupper.v6)
    "dfu6CompileOnly"(sourceSets.main.get().output)
    "dfu8Api"(libs.datafixerupper.v8)
    "dfu8CompileOnly"(sourceSets.main.get().output)

    compileOnly(stubs.output)
}

val dfu6Jar = tasks.register<Jar>("dfu6Jar") {
    archiveClassifier.set("dfu6")
    from(dfu6.output)
}

val dfu8Jar = tasks.register<Jar>("dfu8Jar") {
    archiveClassifier.set("dfu8")
    from(dfu8.output)
}

val mergedDfu6Jar = tasks.register<Jar>("mergedDfu6Jar") {
    archiveClassifier.set("dfu6-merged")
    from(sourceSets.main.get().output)
    from(dfu6.output)
}

val mergedDfu8Jar = tasks.register<Jar>("mergedDfu8Jar") {
    archiveClassifier.set("dfu8-merged")
    from(sourceSets.main.get().output)
    from(dfu8.output)
}

publishing {
    publications {
        val mavenJava = this.getByName<MavenPublication>("mavenJava")
        mavenJava.artifact(dfu6Jar)
        mavenJava.artifact(dfu8Jar)
        mavenJava.artifact(mergedDfu6Jar)
        mavenJava.artifact(mergedDfu8Jar)
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
