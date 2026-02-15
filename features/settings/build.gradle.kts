plugins {
    id("danhdue.android.feature")
    id("danhdue.android.compose")
}

android {
    namespace = "com.danhdue.settings"
}

dependencies {
    implementation(project(":libraries:framework"))
    implementation(project(":libraries:components"))
}
