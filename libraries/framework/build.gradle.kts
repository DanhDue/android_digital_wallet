import extensions.addFirebaseDependencies
import extensions.addFlipperDependencies
import extensions.addNavigationDependencies
import extensions.addNetworkDependencies
import extensions.addStorageDependencies
import extensions.api
import extensions.implementation

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
    id(Deps.COMMONS_DAGGER_HILT)
}

android {
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
    // Cross-feature seam: re-exported so framework consumers keep resolving
    // com.danhdue.platform.EntryProviderInstaller / LocalEntryProviderInstallers.
    api(project(":platform"))
    // Task 5: primitives that moved down to :core (coroutines / extension / pref / room /
    // session / usecase / utils / DataState / calladapter / AppInitializer). Re-exported
    // (`api`) so existing framework consumers — :app and every :features:* — keep resolving
    // them transitively until the full rewire in Task 9.
    api(project(":core"))
    implementation(Deps.multidex)
    // Paging
    implementation(Deps.AndroidX.paging)
    addNetworkDependencies()
    addStorageDependencies()
    addFirebaseDependencies()
    addNavigationDependencies()
    addFlipperDependencies()
}
