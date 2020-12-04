rootProject.name = "kaidl"

include(":kaidl")
include(":kaidl-runtime")
include(":test")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when(requested.id.id) {
                "symbol-processing" -> {
                    useModule("com.google.devtools.ksp:symbol-processing:1.4.10-dev-experimental-20201120")
                }
            }
        }
    }

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}