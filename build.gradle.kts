plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

// Remova a seção allprojects, pois os repositórios devem ser definidos apenas em settings.gradle.kts.
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath ("com.google.gms:google-services:4.3.15") // Verifique a versão mais recente
    }
}
