plugins {
    id("danhdue.android.feature")
    id("danhdue.android.compose")
}

android {
    namespace = "com.danhdue.splash"
}

dependencies {
    implementation(project(":libraries:framework"))
    implementation(project(":libraries:components"))
}
