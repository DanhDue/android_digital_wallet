plugins {
    id("danhdue.android.library")
    id("danhdue.android.compose")
}

android {
    namespace = "com.danhdue.jetframework"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.config)
}
