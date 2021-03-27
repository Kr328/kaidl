// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    rootProject.extra.let {
        it["gGroupId"] = "com.github.kr328.kaidl"

        it["gKotlinVersion"] = "1.4.31"

        it["gCompileSdkVersion"] = 30
        it["gTargetSdkVersion"] = 30
        it["gMinSdkVersion"] = 21

        it["gVersionCode"] = 108
        it["gVersionName"] = "1.8"

        it["gKotlinCoroutineVersion"] = "1.4.3"
        it["gKotlinSymbolVersion"] = "1.4.31-1.0.0-alpha06"
        it["gJunitVersion"] = "4.13.2"
        it["gAndroidXJunitVersion"] = "1.1.2"
        it["gAndroidXEspressoVersion"] = "3.3.0"
        it["gKotlinpoetVersion"] = "1.7.2"
    }

    val gKotlinVersion: String by rootProject.extra

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha12")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$gKotlinVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}