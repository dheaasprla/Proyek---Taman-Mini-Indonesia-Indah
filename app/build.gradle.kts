plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.proyektmii"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.proyektmii"
        minSdk = 19
        targetSdk = 22
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
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
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    // Perbaikan: Menurunkan versi pustaka androidx agar kompatibel dengan targetSdk 22
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
    implementation("androidx.activity:activity-ktx:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.multidex:multidex:2.0.1")

    // Gson
    implementation("com.google.code.gson:gson:2.8.9")

    // .aar lokal (jangan lupa taruh di folder app/libs)
    implementation(files("libs/cloudpossdkV1.5.3.005_Standard.aar"))

    // Material
    implementation("com.google.android.material:material:1.2.0")

    // Unit Test
    testImplementation("junit:junit:4.13.2")

    // Android Test
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation ("androidx.cardview:cardview:1.0.0")

}