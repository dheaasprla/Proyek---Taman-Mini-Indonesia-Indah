plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.proyektmii"
    // MODIFIKASI: Mengubah compileSdk ke versi 25
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.proyektmii"
        minSdk = 21
        // MODIFIKASI: Mengubah targetSdk ke versi 25
        targetSdk = 25
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true // Tambahkan ini untuk mengatasi batas 64K metode

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    // MODIFIKASI: Mengubah versi lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    // MODIFIKASI: Mengubah versi Activity Compose
    implementation("androidx.activity:activity-compose:1.8.0")
    // MODIFIKASI: Mengubah versi BOM
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    // MODIFIKASI: Mengubah versi Material3
    implementation("androidx.compose.material3:material3:1.1.2")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // MODIFIKASI: Mengubah versi animation dan core-ktx
    implementation("androidx.compose.animation:animation:1.5.4")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // MODIFIKASI: Mengubah versi lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation(files("libs/cloudpossdkV1.7.5.1_Standard.aar"))

    implementation("androidx.multidex:multidex:2.0.1")
}