import danhdue.convention.AppConfig
import danhdue.convention.EnvConfigs
import danhdue.convention.addLibDefaultConfig
import danhdue.convention.addComposeConfig

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    // id("codeanalyzetools.quality")
    // id("codeanalyzetools.jacoco-report")
    // id("codeanalyzetools.spotless")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {

    addLibDefaultConfig()
    addComposeConfig()

    kotlinOptions {
        languageVersion = AppConfig.kotlinVersion
        jvmTarget = AppConfig.jvmTarget.target
        freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
    }
}

dependencies {
    // Common
    "api"("com.jakewharton.timber:timber:5.0.1")
    "api"("androidx.core:core-ktx:1.15.0")
    "api"("androidx.appcompat:appcompat:1.7.0")
    "api"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    "api"("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    "api"("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    "api"("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    "api"("androidx.activity:activity-ktx:1.10.0")
    "compileOnly"("org.projectlombok:lombok:1.18.30")
    "annotationProcessor"("org.projectlombok:lombok:1.18.30")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2025.01.00")
    "implementation"(composeBom)
    "androidTestImplementation"(composeBom)

    "implementation"("androidx.compose.ui:ui")
    "implementation"("androidx.compose.material3:material3")
    "implementation"("androidx.compose.material:material-icons-extended")
    "implementation"("androidx.compose.ui:ui-tooling")
    "implementation"("androidx.compose.ui:ui-tooling-preview")
    "implementation"("androidx.compose.runtime:runtime")
    "implementation"("androidx.compose.foundation:foundation")
    
    "implementation"("androidx.hilt:hilt-navigation-compose:1.3.0")
    "implementation"("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    "implementation"("androidx.activity:activity-compose:1.10.0")
    "implementation"("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    "implementation"("androidx.constraintlayout:constraintlayout-compose:1.1.0")
    "implementation"("com.airbnb.android:lottie-compose:6.6.2")
    "implementation"("androidx.paging:paging-runtime:3.3.5")
    "implementation"("androidx.paging:paging-common:3.3.5")
    "implementation"("androidx.paging:paging-compose:3.3.5")
    
    "implementation"("io.coil-kt.coil3:coil-compose:3.0.4")
    "implementation"("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
    "implementation"("androidx.compose.ui:ui-graphics")

    // Compose Testing
    "debugImplementation"("androidx.compose.ui:ui-tooling")
    "debugImplementation"("androidx.compose.ui:ui-test-manifest")
    "androidTestImplementation"("androidx.compose.ui:ui-test-junit4")
    
    // ExcelReader
    "implementation"("org.apache.poi:poi-ooxml:5.2.5")
    
    // Test
    "testImplementation"("junit:junit:4.13.2")
}
