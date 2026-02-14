plugins {
    alias(libs.plugins.danhdue.android.feature)
    alias(libs.plugins.danhdue.android.compose)
}

android {
    namespace = "com.danhdue.wallet"
}

dependencies {
    implementation(project(":libraries:framework"))
    implementation(project(":libraries:components"))
}
