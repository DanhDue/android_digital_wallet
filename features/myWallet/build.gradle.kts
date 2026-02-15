plugins {
    id("danhdue.android.feature")
    id("danhdue.android.compose")
}

android {
    namespace = "com.danhdue.mywallet"
}

dependencies {
    implementation(project(":libraries:framework"))
    implementation(project(":libraries:components"))
}
