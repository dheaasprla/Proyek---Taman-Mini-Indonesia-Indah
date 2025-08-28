plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

apply(plugin = "kotlin-parcelize") // gunakan ini untuk Parcelize

android {
    namespace = "com.example.proyektmii"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.proyektmii"
        minSdk = 19           // tetap 19 sesuai pilihanmu
        targetSdk = 22

        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = true
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

    // Non-Compose project: jangan aktifkan compose
    buildFeatures {
        // compose = false // default false, hanya dokumentasi
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.10.1")

    // AppCompat (pilih versi lama yang kompatibel dengan minSdk 19)
    implementation("androidx.appcompat:appcompat:1.4.2")

    // RecyclerView / CardView
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")

    // Lifecycle (non-compose)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Gson
    implementation("com.google.code.gson:gson:2.8.9")

    // Multidex
    implementation("androidx.multidex:multidex:2.0.1")

    // CloudPOS SDK (lokal AAR)
    implementation(files("libs/cloudpossdkV1.7.5.1_Standard.aar"))

    // Billing (opsional, gunakan versi stabil)
    implementation("com.android.billingclient:billing:5.1.0")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}