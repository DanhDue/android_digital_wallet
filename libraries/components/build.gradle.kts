import extensions.JET_FRAMEWORK
import extensions.addFirebaseDependencies

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}

android {
    namespace = "com.danhdue.libraries.components"
}

dependencies {
    addFirebaseDependencies()
    implementation(Deps.splashScreen)
    JET_FRAMEWORK
}
