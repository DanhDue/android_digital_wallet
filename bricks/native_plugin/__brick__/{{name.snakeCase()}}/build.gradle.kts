/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
import extensions.compileOnly
import extensions.implementation
import extensions.ksp
import extensions.testImplementation

plugins {
    id(Deps.ANDROID_LIBRARY_GRADLE_PLUGIN_ID)
    id(Deps.KOTLIN_GRADLE_PLUGIN_ID)
    id(Deps.KOTLIN_SYMBOL_PROCESSING_PLUGIN_ID)
    id(Deps.ANDROID_COMPOSE_PLUGIN_ID)
}

android {
    namespace = "{{package}}"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = AppConfig.sourceCompatibility
        targetCompatibility = AppConfig.targetCompatibility
    }

    kotlinOptions {
        jvmTarget = AppConfig.jvmTarget.target
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    // Pure Dagger 2
    implementation(Deps.Dagger.core)
    ksp(Deps.Dagger.compiler)

    // Background WorkManager
    implementation(Deps.WorkManager.workRuntimeKtx)

    // Jetpack Compose (Pure, No Hilt)
    implementation(platform(Deps.Compose.composeBOM))
    implementation(Deps.Compose.composeUI)
    implementation(Deps.Compose.material3)
    implementation(Deps.Compose.runtime)
    implementation(Deps.Compose.foundation)
    implementation(Deps.Compose.activityCompose)
    implementation(Deps.Compose.lifecycleViewmodelCompose)
    implementation(Deps.AndroidX.lifecycleViewmodelKtx)

    // Flutter Embedding (Provided by Flutter host at runtime)
    compileOnly(Deps.Flutter.embedding)

    // Testing
    testImplementation(Deps.Test.junit)
    testImplementation(Deps.Test.mockk)
    testImplementation(Deps.Test.robolectric)
    testImplementation(Deps.Kotlin.coroutineTest)
    testImplementation(Deps.Test.turbine)
    testImplementation(Deps.WorkManager.workTesting)
    testImplementation(Deps.Flutter.embedding)
}
