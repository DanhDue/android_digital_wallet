import extensions.FRAMEWORK
import extensions.UI_KIT
import extensions.addNavigationDependencies

plugins {
    id(Deps.COMMONS_ANDROID_FEATURE)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}

android {
    namespace = "com.danhdue.trends"
}

dependencies {
    FRAMEWORK
    UI_KIT

    addNavigationDependencies()
}
