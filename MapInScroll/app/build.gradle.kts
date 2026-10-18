plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "de.afarber.mapinscroll"
    compileSdk = 37

    defaultConfig {
        applicationId = "de.afarber.mapinscroll"
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.openmapview)
}
