plugins {
    alias(libs.plugins.android.application) // Usando alias do TOML para o plugin Android
    alias(libs.plugins.kotlin.android) // Plugin Kotlin para Android
    alias(libs.plugins.google.gms.google.services) // Plugin Google Services
}

android {
    namespace = "com.example.myapplication" // Define o namespace do app
    compileSdk = 34 // Versão do SDK de compilação

    defaultConfig {
        applicationId = "com.example.myapplication" // ID do aplicativo
        minSdk = 24 // SDK mínimo suportado
        targetSdk = 34 // Versão de SDK de destino
        versionCode = 1 // Código da versão do app
        versionName = "1.0" // Nome da versão

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Runner de testes
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Desativa a minificação no build de release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), // Arquivo Proguard padrão
                "proguard-rules.pro" // Regras Proguard específicas do projeto
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // Compatível com Java 8
        targetCompatibility = JavaVersion.VERSION_1_8 // Alvo para Java 8
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true // Habilita ViewBinding
    }

    kotlinOptions {
        jvmTarget = "1.8" // Configuração do target JVM
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation(libs.core.ktx)
    implementation(libs.support.annotations)
    implementation(libs.kotlinx.coroutines.android)

    // Only include Data Binding directly
    implementation("androidx.databinding:databinding-runtime:8.6.1")
    implementation(libs.drawerlayout)
    implementation(libs.firebase.crashlytics.buildtools)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.4.0")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.4.0")

    implementation ("com.android.volley:volley:1.2.1")

}

