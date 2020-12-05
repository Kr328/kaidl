// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    rootProject.extra.let {
        it["gGroupId"] = "com.github.kr328.kaidl"

        it["gKotlinVersion"] = "1.4.10"

        it["gCompileSdkVersion"] = 30
        it["gTargetSdkVersion"] = 30
        it["gMinSdkVersion"] = 21

        it["gVersionCode"] = 1
        it["gVersionName"] = "1.0"

        it["gKotlinCoroutineVersion"] = "1.4.2"
        it["gKotlinSymbolVersion"] = "1.4.10-dev-experimental-20201120"
        it["gJunitVersion"] = "4.13.1"
        it["gAndroidXJunitVersion"] = "1.1.2"
        it["gAndroidXEspressoVersion"] = "3.3.0"
        it["gKotlinpoetVersion"] = "1.7.2"
    }

    val gKotlinVersion: String by rootProject.extra

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-beta01")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$gKotlinVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}