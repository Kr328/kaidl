@file:Suppress("UNUSED_VARIABLE")

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import java.util.*

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(androidv.gradle)
        classpath(kotlinv.ksp.gradle)
        classpath(kotlinv.gradle)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    group = "com.github.kr328.kaidl"
    version = "1.15"

    afterEvaluate {
        val android = extensions.findByType(BaseExtension::class)?.apply {
            val isLibrary = this is LibraryExtension

            compileSdkVersion(30)

            defaultConfig {
                minSdk = 21
                targetSdk = 30

                versionName = version.toString()
                versionCode = version.toString()
                    .split(".")
                    .joinToString(separator = "") { "%03d".format(it.toInt()) }
                    .toInt(10)

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                if (isLibrary) {
                    consumerProguardFiles("consumer-rules.pro")
                }
            }

            buildTypes {
                named("release") {
                    isMinifyEnabled = false
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }
        }

        afterEvaluate {
            extensions.findByType(PublishingExtension::class)?.apply {
                val sourcesJar = tasks.register("sourcesJar", type = Jar::class) {
                    archiveClassifier.set("sources")

                    if (android != null) {
                        from(android.sourceSets["main"].java.srcDirs)
                    } else {
                        from((project.extensions.getByName("sourceSets") as SourceSetContainer)["main"].allSource)
                    }
                }

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

                        from(components.findByName("release") ?: components["java"])

                        artifact(sourcesJar)

                        groupId = project.group.toString()
                        artifactId = project.name
                        version = project.version.toString()
                    }
                }

                repositories {
                    val publishFile = rootProject.file("publish.properties")
                    if (publishFile.exists()) {
                        val publish = Properties().apply { publishFile.inputStream().use(this::load) }

                        maven {
                            url = uri(publish.getProperty("publish.url")!!)

                            credentials {
                                username = publish.getProperty("publish.user")!!
                                password = publish.getProperty("publish.password")!!
                            }
                        }
                    }
                }
            }
        }
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}
