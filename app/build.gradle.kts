import danhdue.convention.AppConfig
import danhdue.convention.EnvConfigs

plugins {
    id("danhdue.android.application")
    id("danhdue.android.compose")
    id("danhdue.android.hilt")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    id("com.diffplug.spotless")
    id("codeanalyzetools.quality")
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = AppConfig.namespace
    defaultConfig {
        applicationId = AppConfig.applicationId
        // targetSdk, versionCode, versionName are set by danhdue.android.application plugin
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.multidex)

    // Common
    implementation(libs.timber)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.ktx)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

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

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.work.multiprocess)
    implementation(libs.androidx.hilt.work)
    androidTestImplementation(libs.hilt.testing)

    // Navigation 3
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.androidx.navigation.common.ktx)

    // LeakCanary
    debugImplementation(libs.leakcanary.android)
    debugImplementation(libs.flipper.leakcanary.plugin)

    // Modules
    implementation(project(":libraries:components"))
    implementation(project(":libraries:framework"))
    implementation(project(":libraries:jetframework"))
    implementation(project(":libraries:testutils"))


    implementation(project(":domain:authenticator"))

    implementation(project(":features:authentication"))
    implementation(project(":features:home"))
    implementation(project(":features:myWallet"))
    implementation(project(":features:transactions"))
    implementation(project(":features:scanner"))
    implementation(project(":features:trends"))
    implementation(project(":features:settings"))
    implementation(project(":features:splash"))
}
