rootProject.name = "kaidl"

include(":kaidl")
include(":kaidl-runtime")
include(":test")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}