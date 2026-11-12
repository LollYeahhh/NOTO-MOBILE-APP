plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Kunci rilis: keystore.properties (tidak di-commit) menunjuk ke file .jks dan file sandi di luar repo.
// Kalau file ini tidak ada, build release tetap jalan tapi tidak ditandatangani.
val kunciRilis = rootProject.file("keystore.properties").takeIf { it.exists() }
    ?.let { f -> java.util.Properties().apply { f.inputStream().use { load(it) } } }

android {
    namespace = "id.noto.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "id.noto.app"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (kunciRilis != null) create("release") {
            val sandi = file(kunciRilis.getProperty("passwordFile")).readText().trim()
            storeFile = file(kunciRilis.getProperty("storeFile"))
            storePassword = sandi
            keyAlias = kunciRilis.getProperty("keyAlias")
            keyPassword = sandi
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

// Skema database disimpan di app/schemas (ikut di-commit) sebagai acuan migrasi versi berikutnya.
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}