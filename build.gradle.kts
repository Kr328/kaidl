@file:Suppress("UNUSED_VARIABLE")

buildscript {
    val kotlinVersion = "1.5.10"

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    extra.apply {
        val moduleId: String by extra("com.github.kr328.kaidl")

        val buildTargetSdk: Int by extra(30)
        val buildMinSdk: Int by extra(21)

        val buildVersionCode: Int by extra(112)
        val buildVersionName: String by extra("1.12")

        val coroutineVersion: String by extra("1.5.0")
        val kotlinSymbolVersion: String by extra("1.5.10-1.0.0-beta01")
        val junitVersion: String by extra("4.13.2")
        val androidJunitVersion: String by extra("1.1.2")
        val espressoVersion: String by extra("3.3.0")
        val kotlinpoetVersion: String by extra("1.8.0")
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}
