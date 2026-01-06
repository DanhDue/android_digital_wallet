import commons.addLibDefaultConfig
import extensions.addNetworkDependencies
import extensions.addTestDependencies

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}

android {
    namespace = "com.danhdue.libraries.testutils"
    addLibDefaultConfig()
}

dependencies {
    addTestDependencies()
    api(Deps.Test.hamcrest)
    api(Deps.Test.hamcrestCore)
    api(Deps.Test.truth)
    api(Deps.Test.turbine)
    api(Deps.Test.mockwebserver)
    api(Deps.Test.json)
    api(Deps.Test.robolectric)
    addNetworkDependencies()
}
