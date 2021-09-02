@file:Suppress("UNUSED_VARIABLE")

buildscript {
    val agpVerson = "4.2.1"
    val kotlinVersion = "1.5.30"
    val kspVersion = "1.5.30-1.0.0-beta09"

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:$agpVerson")
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:$kspVersion")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }

    allprojects {
        repositories {
            google()
            mavenCentral()
        }

        val moduleId: String by extra("com.github.kr328.kaidl")

        val buildTargetSdk: Int by extra(30)
        val buildMinSdk: Int by extra(21)

        val buildVersionCode: Int by extra(113)
        val buildVersionName: String by extra("1.13")

        val coroutineVersion: String by extra("1.5.1")
        val kotlinSymbolVersion: String by extra(kspVersion)
        val junitVersion: String by extra("4.13.2")
        val androidJunitVersion: String by extra("1.1.3")
        val espressoVersion: String by extra("3.4.0")
        val kotlinpoetVersion: String by extra("1.9.0")
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}
