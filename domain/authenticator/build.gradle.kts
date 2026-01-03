import extensions.FRAMEWORK
import extensions.TEST

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_DAGGER_HILT)
}

android {
    namespace = "com.danhdue.authenticator"
}

dependencies {
    FRAMEWORK
    TEST
}
