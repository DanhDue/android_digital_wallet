package commons

import extensions.TEST
import extensions.addCommonDependencies
import extensions.addComposeDependencies

plugins {
    id("commons.android-library")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {

    addComposeConfig()

    kotlinOptions {
        languageVersion = AppConfig.kotlinVersion
        jvmTarget = AppConfig.jvmTarget.target
        freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
    }
}

dependencies {
    addCommonDependencies()
    addComposeDependencies()
    TEST
}
