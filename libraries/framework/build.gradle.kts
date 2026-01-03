import extensions.addFirebaseDependencies
import extensions.addNetworkDependencies
import extensions.addStorageDependencies
import extensions.implementation

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}

android {
    namespace = "com.danhdue.framework"
}

dependencies {
    implementation(Deps.multidex)
    // Paging
    implementation(Deps.AndroidX.paging)
    addNetworkDependencies()
    addStorageDependencies()
    addFirebaseDependencies()
}
