import extensions.COMPONENT
import extensions.FEATURE_MY_WALLET
import extensions.FEATURE_SCANNER
import extensions.FEATURE_SETTINGS
import extensions.FEATURE_TRANSACTIONS
import extensions.FEATURE_TRENDS
import extensions.FRAMEWORK
import extensions.addNavigationDependencies

plugins {
    id(Deps.COMMONS_ANDROID_FEATURE)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}

android {
    namespace = "com.danhdue.home"
}

dependencies {
    FRAMEWORK
    COMPONENT

    FEATURE_MY_WALLET
    FEATURE_TRANSACTIONS
    FEATURE_SCANNER
    FEATURE_TRENDS
    FEATURE_SETTINGS

    addNavigationDependencies()
}
