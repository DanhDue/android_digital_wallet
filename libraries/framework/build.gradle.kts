import extensions.addFirebaseDependencies
import extensions.addNetworkDependencies
import extensions.addStorageDependencies
import extensions.implementation
import Deps
import commons.addLibDefaultConfig

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}
android {
    namespace = "com.danhdue.framework"
    addLibDefaultConfig()

    kotlinOptions {
        languageVersion = AppConfig.kotlinVersion
        jvmTarget = AppConfig.jvmTarget.target
        freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
    }
}

dependencies {
    implementation(Deps.multidex)
    // Paging
    implementation(Deps.AndroidX.paging)
    addNetworkDependencies()
    addStorageDependencies()
    addFirebaseDependencies()
}