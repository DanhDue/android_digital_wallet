package commons

import extensions.addHiltDependencies

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
}

dependencies { addHiltDependencies() }
