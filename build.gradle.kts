plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

// Remova a seção allprojects, pois os repositórios devem ser definidos apenas em settings.gradle.kts.
