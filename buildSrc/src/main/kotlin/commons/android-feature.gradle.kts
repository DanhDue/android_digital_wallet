package commons

import AppConfig
import EnvConfigs
import extensions.TEST
import extensions.addCommonDependencies
import extensions.addNavigationDependencies
import extensions.buildBooleanConfigField
import extensions.buildStringConfigField

plugins {
    id("commons.android-library")
    id("commons.dagger-hilt")
    id("codeanalyzetools.quality")
    id("codeanalyzetools.jacoco-report")
    id("codeanalyzetools.spotless")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Release.dbName)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, EnvConfigs.Release.crashlyticsEnable)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, EnvConfigs.Release.analyticsEnable)
        }

        debug {
            buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Debug.dbName)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, EnvConfigs.Debug.crashlyticsEnable)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, EnvConfigs.Debug.analyticsEnable)
        }
    }

    compileOptions {
        sourceCompatibility = AppConfig.sourceCompatibility
        targetCompatibility = AppConfig.targetCompatibility
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    kotlin {
        compilerOptions {
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(AppConfig.kotlinVersion))
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(AppConfig.kotlinVersion))
            jvmTarget.set(AppConfig.jvmTarget)
            freeCompilerArgs.addAll(EnvConfigs.FreeCompilerArgs)
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

android.libraryVariants.all {
    val variantName = name
    kotlin.sourceSets {
        getByName("main") {
            kotlin.srcDir(File("build/generated/ksp/$variantName/kotlin"))
        }
        getByName("test") {
            kotlin.srcDir(File("build/generated/ksp/$variantName/kotlin"))
        }
        getByName("debug") {
            kotlin.srcDir(File("build/generated/ksp/$variantName/kotlin"))
        }
        getByName("release") {
            kotlin.srcDir(File("build/generated/ksp/$variantName/kotlin"))
        }
    }
}

dependencies {
    coreLibraryDesugaring(Deps.OpenTelemetry.desugaring)
    // Common
    addCommonDependencies()
    // compose navigation
    addNavigationDependencies()
    // Test
    TEST
}
