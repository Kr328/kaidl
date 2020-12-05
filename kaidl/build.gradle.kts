import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    id("application")
    id("kotlin")
    id("maven")
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.5"
}

val gGroupId: String by rootProject.extra
val gVersionName: String by rootProject.extra
val gKotlinVersion: String by rootProject.extra
val gKotlinSymbolVersion: String by rootProject.extra
val gKotlinpoetVersion: String by rootProject.extra

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType(KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

publishing {
    publications {
        create("Bintray", type = MavenPublication::class) {
            from(components["java"])

            groupId = gGroupId
            artifactId = "kaidl"

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
        name = "kaidl"

        version.apply {
            name = gVersionName
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$gKotlinSymbolVersion")
    implementation("com.squareup:kotlinpoet:$gKotlinpoetVersion")
}
