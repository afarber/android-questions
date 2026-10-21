plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "de.afarber.mycoordinator"
    compileSdk = 37

    defaultConfig {
        applicationId = "de.afarber.mycoordinator"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.palette)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso)
}
