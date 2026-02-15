plugins {
    id("danhdue.android.library")
    id("danhdue.android.compose")
}

android {
    namespace = "com.danhdue.libraries.components"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.config)

    implementation(libs.androidx.core.splashscreen)
    implementation(project(":libraries:jetframework"))
}
