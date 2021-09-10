plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.google.devtools.ksp")
}

dependencies {
    ksp(project(":kaidl"))
    implementation(project(":kaidl-runtime"))

    implementation(kotlin("stdlib"))
    implementation(kotlinv.coroutine)
    testImplementation(testingv.junit.jvm)
    androidTestImplementation(testingv.junit.android)
    androidTestImplementation(testingv.espresso)
}
