plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "de.afarber.testscroll2"
    compileSdk = 37

    defaultConfig {
        applicationId = "de.afarber.testscroll2"
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
    implementation(libs.appcompat)
}
