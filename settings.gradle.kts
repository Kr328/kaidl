rootProject.name = "kaidl"

include(":kaidl")
include(":kaidl-runtime")
include(":test")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when(requested.id.id) {
                "symbol-processing" -> {
                    useModule("com.google.devtools.ksp:symbol-processing:1.4.20-dev-experimental-20201204")
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