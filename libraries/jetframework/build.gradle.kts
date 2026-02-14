import extensions.addFirebaseDependencies

plugins {
    alias(libs.plugins.danhdue.android.library)
    alias(libs.plugins.danhdue.android.compose)
}

android {
    namespace = "com.danhdue.jetframework"
}

dependencies {
    addFirebaseDependencies()
}
