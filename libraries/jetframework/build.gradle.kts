import extensions.addFirebaseDependencies

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}

android {
    namespace = "com.danhdue.jetframework"
}

dependencies {
    addFirebaseDependencies()
}
