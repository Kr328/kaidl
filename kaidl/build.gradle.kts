plugins {
    id("application")
    id("kotlin")
}

val gKotlinVersion: String by rootProject.extra
val gKotlinSymbolVersion: String by rootProject.extra
val gKotlinpoetVersion: String by rootProject.extra

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$gKotlinSymbolVersion")
    implementation("com.squareup:kotlinpoet:$gKotlinpoetVersion")
}
