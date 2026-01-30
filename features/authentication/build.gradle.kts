import extensions.COMPONENT
import extensions.FRAMEWORK
import extensions.addNavigationDependencies
import extensions.addNetworkDependencies

plugins {
    id(Deps.COMMONS_ANDROID_FEATURE)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}

android {
    namespace = "com.danhdue.authentication"
}

dependencies {
    FRAMEWORK
    COMPONENT

    addNetworkDependencies()
    addNavigationDependencies()
}
