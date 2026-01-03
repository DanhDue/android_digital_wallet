import commons.addLibDefaultConfig
import extensions.addFirebaseDependencies

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}

android {
    namespace = "com.danhdue.libraries.components"
    addLibDefaultConfig()
}

dependencies {
    addFirebaseDependencies()
    implementation(Deps.splashScreen)
}