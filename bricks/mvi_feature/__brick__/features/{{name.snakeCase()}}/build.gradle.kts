
plugins {
    id("danhdue.android.feature")
    id("danhdue.android.compose")
}

android {
    namespace = "{{package}}"
}

dependencies {
    implementation(project(":libraries:framework"))
    implementation(project(":libraries:components"))
}
