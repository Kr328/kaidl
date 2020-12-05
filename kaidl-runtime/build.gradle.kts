import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

tasks.withType(KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = "1.7"
    }
}

dependencies {

}