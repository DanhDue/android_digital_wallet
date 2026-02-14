import extensions.addNetworkDependencies
import extensions.addTestDependencies

plugins {
    alias(libs.plugins.danhdue.android.library)
    alias(libs.plugins.danhdue.android.compose)
}

android {
    namespace = "com.danhdue.libraries.testutils"
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
