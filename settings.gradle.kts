rootProject.name = "kaidl"

include(":kaidl")
include(":kaidl-runtime")
include(":test")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when(requested.id.id) {
                "symbol-processing" -> {
                    useModule("com.google.devtools.ksp:symbol-processing:${requested.version}")
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