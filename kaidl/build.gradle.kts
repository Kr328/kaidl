import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("application")
    kotlin("jvm")
    `maven-publish`
}

val moduleId: String by extra

val buildTargetSdk: Int by extra
val buildMinSdk: Int by extra

val buildVersionName: String by extra
val kotlinSymbolVersion: String by extra
val kotlinpoetVersion: String by extra

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
        create("release", type = MavenPublication::class) {
            pom {
                name.set("kaidl")
                description.set("Generate AIDL-like android binder interface with Kotlin")
                url.set("https://github.com/Kr328/kaidl")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        id.set("kr328")
                        name.set("Kr328")
                        email.set("kr328app@outlook.com")
                    }
                }
            }

            from(components["java"])

            groupId = moduleId
            artifactId = "kaidl"

            version = buildVersionName
        }
    }

    repositories {
        maven {
            url = uri("${rootProject.buildDir}/release")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:$kotlinSymbolVersion")
    implementation("com.squareup:kotlinpoet:$kotlinpoetVersion")
}
