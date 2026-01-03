import commons.addLibDefaultConfig
import extensions.addNetworkDependencies
import extensions.addTestDependencies

plugins {
    id("android.core.library.plugin")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.danhdue.libraries.testutils"
    addLibDefaultConfig()

    kotlinOptions {
        languageVersion = AppConfig.kotlinVersion
        jvmTarget = AppConfig.jvmTarget.target
        freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
    }
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
