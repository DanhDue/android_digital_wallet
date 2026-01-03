import extensions.FRAMEWORK
import extensions.addJsonParsingDependencies
import extensions.addRoomDependencies

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_DAGGER_HILT)
}

android {
    namespace = "com.danhdue.repositories"
}

dependencies {
    FRAMEWORK
    // Moshi
    addJsonParsingDependencies()
    // Room
    addRoomDependencies()
}
