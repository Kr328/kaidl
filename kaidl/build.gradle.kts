plugins {
    id("application")
    id("kotlin")
}

val kotlinVersion: String by rootProject.extra

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.4.10-dev-experimental-20201120")
    implementation("com.squareup:kotlinpoet:1.7.2")
}
