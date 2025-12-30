package commons

import extensions.TEST
import extensions.addCommonDependencies
import extensions.addComposeDependencies

plugins {
    id("commons.android-library")
}

android { addComposeConfig() }

dependencies {
    addCommonDependencies()
    addComposeDependencies()
    TEST
}
