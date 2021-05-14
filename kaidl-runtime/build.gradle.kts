import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("kotlin-android")
    `maven-publish`
}

val moduleId: String by extra

val buildTargetSdk: Int by extra
val buildMinSdk: Int by extra

val buildVersionCode: Int by extra
val buildVersionName: String by extra

val coroutineVersion: String by extra

android {
    compileSdk = buildTargetSdk

    defaultConfig {
        minSdk = buildMinSdk
        targetSdk = buildTargetSdk

        versionCode = buildVersionCode
        versionName = buildVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
}

afterEvaluate {
    publishing {
        publications {
            create("release", type = MavenPublication::class) {
                pom {
                    name.set("kaidl-runtime")
                    description.set("Generate AIDL-like android binder interface with Kotlin (Runtime)")
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

                from(components["release"])

                groupId = moduleId
                artifactId = "kaidl-runtime"

                version = buildVersionName
            }
        }

        repositories {
            maven {
                url = uri("${rootProject.buildDir}/release")
            }
        }
    }
}