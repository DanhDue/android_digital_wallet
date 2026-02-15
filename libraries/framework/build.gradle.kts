import danhdue.convention.EnvConfigs

plugins {
    id("danhdue.android.library")
    id("danhdue.android.compose")
    id("danhdue.android.hilt")
}

android {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xexplicit-api=warning"
    }

    namespace = "com.danhdue.framework"
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        getByName("release") {
            buildConfigField("String", "BASE_URL", "\"${EnvConfigs.Production.BASE_URL}\"")
        }
        getByName("debug") {
            buildConfigField("String", "BASE_URL", "\"${EnvConfigs.Development.BASE_URL}\"")
        }
    }
}

dependencies {
    implementation(libs.androidx.multidex)
    // Paging
    implementation(libs.androidx.paging.runtime)

    // Network
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
    implementation(libs.moshi.lazy.adapter)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    testImplementation(libs.okhttp.mockwebserver)
    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    // Storage
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.security.crypto.ktx)
    implementation(libs.tink.android)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.config)

    // Navigation
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.kotlinx.serialization.core)

    // Flipper
    debugImplementation(libs.flipper)
    debugImplementation(libs.flipper.network.plugin)
    debugImplementation(libs.soloader)
    releaseImplementation(libs.flipper.noop)
}
