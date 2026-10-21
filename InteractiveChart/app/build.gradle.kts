plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.android.interactivechart"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.android.interactivechart"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.core)
}
