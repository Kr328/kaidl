@file:Suppress("UNUSED_VARIABLE")

buildscript {
    val kotlinVersion = "1.4.32"

    repositories {
        google()
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }

    extra.apply {
        val moduleId: String by extra("com.github.kr328.kaidl")

        val buildTargetSdk: Int by extra(30)
        val buildMinSdk: Int by extra(21)

        val buildVersionCode: Int by extra(109)
        val buildVersionName: String by extra("1.9")

        val coroutineVersion: String by extra("1.4.3")
        val kotlinSymbolVersion: String by extra("1.4.31-1.0.0-alpha06")
        val junitVersion: String by extra("4.13.2")
        val androidJunitVersion: String by extra("1.1.2")
        val espressoVersion: String by extra("3.3.0")
        val kotlinpoetVersion: String by extra("1.7.2")
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}
