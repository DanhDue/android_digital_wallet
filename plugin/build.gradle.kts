import extensions.compileOnly
import extensions.implementation
import extensions.ksp
import extensions.testImplementation

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.ANDROID_COMPOSE_PLUGIN_ID)
}

android {
    namespace = "com.danhdue.plugin"

    buildFeatures {
        compose = true
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
    testImplementation(Deps.WorkManager.workTesting)
    testImplementation(Deps.Test.turbine)
    testImplementation(Deps.Flutter.embedding)
}
