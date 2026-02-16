

plugins {
    id("danhdue.android.feature")
    id("danhdue.android.compose")
}

android {
    namespace = "com.danhdue.authentication"
}

dependencies {
    implementation(project(":libraries:framework"))
    implementation(project(":libraries:components"))

    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
}
