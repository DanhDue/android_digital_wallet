/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
plugins {
    id(Deps.ANDROID_GRADLE_PLUGIN_ID)
    id(Deps.KOTLIN_GRADLE_PLUGIN_ID)
    id(Deps.ANDROID_COMPOSE_PLUGIN_ID)
}

android {
    namespace = "com.danhdue.sample"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = "com.danhdue.sample"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = 1
        versionName = "1.0.0"
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
        unitTests.isIncludeAndroidResources = true
    }

    packaging {
        resources.excludes.apply {
            add("META-INF/AL2.0")
            add("META-INF/LGPL2.1")
        }
    }
}

dependencies {
    implementation(project(":plugin"))

    // Jetpack Compose (Pure, No Hilt)
    implementation(platform(Deps.Compose.composeBOM))
    implementation(Deps.Compose.composeUI)
    implementation(Deps.Compose.material3)
    implementation(Deps.Compose.runtime)
    implementation(Deps.Compose.foundation)
    implementation(Deps.Compose.activityCompose)
    implementation(Deps.Compose.lifecycleViewmodelCompose)

    // WorkManager
    implementation(Deps.WorkManager.workRuntimeKtx)

    // Testing
    testImplementation(Deps.Test.junit)
    testImplementation(Deps.Test.robolectric)
    testImplementation(Deps.WorkManager.workTesting)
}
