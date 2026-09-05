import extensions.FRAMEWORK
import extensions.NETWORK
import extensions.UI_KIT
import extensions.addJsonParsingDependencies
import extensions.addNavigationDependencies

plugins {
    id(Deps.COMMONS_ANDROID_FEATURE)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}

android {
    namespace = "com.danhdue.settings"
}

dependencies {
    FRAMEWORK
    NETWORK
    UI_KIT

    addJsonParsingDependencies()
    addNavigationDependencies()
}
