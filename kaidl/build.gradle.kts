import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("application")
    id("kotlin")
    id("maven")
    id("maven-publish")
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

            groupId = gGroupId
            artifactId = "kaidl"

            version = gVersionName
        }
    }
    repositories {
        maven {
            url = uri("${rootProject.buildDir}/release")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$gKotlinSymbolVersion")
    implementation("com.squareup:kotlinpoet:$gKotlinpoetVersion")
}
