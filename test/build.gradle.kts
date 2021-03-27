plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.google.devtools.ksp") version "1.4.31-1.0.0-alpha06"
}

val gCompileSdkVersion: Int by rootProject.extra
val gTargetSdkVersion: Int by rootProject.extra
val gMinSdkVersion: Int by rootProject.extra

val gVersionCode: Int by rootProject.extra
val gVersionName: String by rootProject.extra

val gKotlinVersion: String by rootProject.extra
val gKotlinCoroutineVersion: String by rootProject.extra
val gJunitVersion: String by rootProject.extra
val gAndroidXJunitVersion: String by rootProject.extra
val gAndroidXEspressoVersion: String by rootProject.extra

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
    ksp(project(":kaidl"))
    implementation(project(":kaidl-runtime"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$gKotlinCoroutineVersion")
    testImplementation("junit:junit:$gJunitVersion")
    androidTestImplementation("androidx.test.ext:junit:$gAndroidXJunitVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$gAndroidXEspressoVersion")
}

afterEvaluate {
    tasks["assembleDebug"].dependsOn(tasks["kspDebugKotlin"])
    tasks["assembleRelease"].dependsOn(tasks["kspReleaseKotlin"])
}