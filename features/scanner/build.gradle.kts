import extensions.COMPONENT
import extensions.FRAMEWORK
import extensions.addNavigationDependencies

plugins {
    id(Deps.COMMONS_ANDROID_FEATURE)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}

android {
    namespace = "com.danhdue.scanner"
}

dependencies {
    FRAMEWORK
    COMPONENT

    addNavigationDependencies()
}
