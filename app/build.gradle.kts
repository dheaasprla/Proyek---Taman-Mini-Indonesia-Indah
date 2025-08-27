plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.proyektmii"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.proyektmii"
        minSdk = 19
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        // Baris ini adalah solusinya
        freeCompilerArgs += "-opt-in=kotlin.experimental.ExperimentalStdlibApi"
    }
}

dependencies {
    // Dependensi inti yang kompatibel dengan API 25
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-ktx:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // Dependensi lain
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation(files("libs/cloudpossdkV1.7.5.1_Standard.aar"))

    // Dependensi untuk pengujian
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation ("androidx.appcompat:appcompat:1.6.1")
}