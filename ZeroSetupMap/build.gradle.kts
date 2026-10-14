plugins {
    alias(libs.plugins.android.application) apply false
    // Not applied, only puts a Kotlin compiler newer than the one bundled with AGP on the classpath
    alias(libs.plugins.kotlin.android) apply false
}
