import extensions.addFirebaseDependencies
import extensions.addFlipperDependencies
import extensions.addNavigationDependencies
import extensions.addNetworkDependencies
import extensions.addStorageDependencies
import extensions.implementation

plugins {
    alias(libs.plugins.danhdue.android.library)
    alias(libs.plugins.danhdue.android.compose)
    alias(libs.plugins.danhdue.android.hilt)
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
            buildConfigField("String", "BASE_URL", "\"${EnvConfigs.Release.BASE_URL}\"")
        }
        getByName("debug") {
            buildConfigField("String", "BASE_URL", "\"${EnvConfigs.Debug.BASE_URL}\"")
        }
    }
}

dependencies {
    implementation(Deps.multidex)
    // Paging
    implementation(Deps.AndroidX.paging)
    addNetworkDependencies()
    addStorageDependencies()
    addFirebaseDependencies()
    // addNavigationDependencies()
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.kotlinx.serialization.core)

    addFlipperDependencies()
}
