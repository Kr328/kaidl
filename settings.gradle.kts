rootProject.name = "kaidl"

include(":kaidl")
include(":kaidl-runtime")
include(":example")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}