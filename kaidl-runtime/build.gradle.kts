plugins {
    id("com.android.library")
    id("kotlin-android")
    `maven-publish`
}

dependencies {
    compileOnly(kotlinv.coroutine)
}