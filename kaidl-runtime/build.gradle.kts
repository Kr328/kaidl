import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    id("kotlin")
    id("maven")
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.5"
}

val gGroupId: String by rootProject.extra
val gVersionName: String by rootProject.extra

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

tasks.withType(KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = "1.6"
    }
}

publishing {
    publications {
        create("Bintray", type = MavenPublication::class) {
            from(components["java"])

            groupId = gGroupId
            artifactId = "kaidl-runtime"

            version = gVersionName
        }
    }
}

bintray {
    val properties = try {
        Properties().apply {
            rootProject.file("local.properties").inputStream().use {
                load(it)
            }
        }
    } catch (e: Exception) {
        return@bintray
    }

    user = properties.getProperty("bintray.user")
    key = properties.getProperty("bintray.key")

    setPublications("Bintray")

    pkg.apply {
        repo = "kaidl"
        name = "kaidl-runtime"

        version.apply {
            name = gVersionName
        }
    }
}

dependencies {

}