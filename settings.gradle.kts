enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "kaidl"

include(":kaidl")
include(":kaidl-runtime")
include(":example")

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
    }
    versionCatalogs {
        create("kotlinv") {
            val common = "1.5.30"
            val coroutine = "1.5.2"
            val ksp = "1.5.30-1.0.0-beta09"
            val poet = "1.9.0"

            alias("gradle").to("org.jetbrains.kotlin:kotlin-gradle-plugin:$common")
            alias("coroutine").to("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutine")
            alias("poet").to("com.squareup:kotlinpoet:$poet")
            alias("ksp-api").to("com.google.devtools.ksp:symbol-processing-api:$ksp")
            alias("ksp-gradle").to("com.google.devtools.ksp:symbol-processing-gradle-plugin:$ksp")
        }
        create("androidv") {
            val plugin = "4.2.1"

            alias("gradle").to("com.android.tools.build:gradle:$plugin")
        }
        create("testingv") {
            val junit = "4.13.2"
            val androidJunit = "1.1.3"
            val espresso = "3.4.0"

            alias("junit-jvm").to("junit:junit:$junit")
            alias("junit.android").to("androidx.test.ext:junit:$androidJunit")
            alias("espresso").to("androidx.test.espresso:espresso-core:$espresso")
        }
    }
}