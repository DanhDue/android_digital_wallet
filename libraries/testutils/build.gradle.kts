plugins {
    id("danhdue.android.library")
    id("danhdue.android.compose")
}

android {
    namespace = "com.danhdue.libraries.testutils"
}

dependencies {
    // Test Dependencies
    api(libs.junit)
    api(libs.androidx.junit)
    api(libs.androidx.junit) // junitKtx same?
    api(libs.coroutines.test)
    api(libs.mockk)

    api(libs.truth)
    api(libs.turbine)
    api(libs.okhttp.mockwebserver)
    api(libs.json)
    api(libs.robolectric)

    // Network Dependencies (addNetworkDependencies)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
    implementation(libs.moshi.lazy.adapter)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
}
