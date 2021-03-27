import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven")
    id("maven-publish")
}

val gCompileSdkVersion: Int by rootProject.extra
val gTargetSdkVersion: Int by rootProject.extra
val gMinSdkVersion: Int by rootProject.extra

val gGroupId: String by rootProject.extra
val gVersionCode: Int by rootProject.extra
val gVersionName: String by rootProject.extra

val gKotlinCoroutineVersion: String by rootProject.extra

android {
    compileSdkVersion(gCompileSdkVersion)

    defaultConfig {
        minSdkVersion(gMinSdkVersion)
        targetSdkVersion(gTargetSdkVersion)

        versionCode = gVersionCode
        versionName = gVersionName

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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$gKotlinCoroutineVersion")
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

                groupId = gGroupId
                artifactId = "kaidl-runtime"

                version = gVersionName
            }
        }
        repositories {
            maven {
                url = uri("${rootProject.buildDir}/release")
            }
        }
    }
}