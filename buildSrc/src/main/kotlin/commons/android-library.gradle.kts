package commons

import AppConfig
import EnvConfigs
import extensions.TEST
import extensions.addCommonDependencies
import extensions.addTestDependencies
import extensions.buildBooleanConfigField
import extensions.buildStringConfigField
import gradle.kotlin.dsl.accessors._01b08877eb0139cc8c320b8760ecef58.kotlinOptions

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("codeanalyzetools.quality")
    id("codeanalyzetools.jacoco-report")
    id("codeanalyzetools.spotless")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {

    addLibDefaultConfig()

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            enableUnitTestCoverage = false
            enableAndroidTestCoverage = false
            manifestPlaceholders[EnvConfigs.BuildConfigKey.crashlyticsEnableKey] = true
            manifestPlaceholders[EnvConfigs.BuildConfigKey.analyticsEnableKey] = true

            proguardFiles(
                getDefaultProguardFile(AppConfig.proguardOptimizedFileName),
                AppConfig.proguardConsumerRules
            )

            buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Release.dbName)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, EnvConfigs.Release.crashlyticsEnable)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, EnvConfigs.Release.analyticsEnable)
        }

        debug {
            enableUnitTestCoverage = false
            enableAndroidTestCoverage = false
            manifestPlaceholders[EnvConfigs.BuildConfigKey.crashlyticsEnableKey] = false
            manifestPlaceholders[EnvConfigs.BuildConfigKey.analyticsEnableKey] = false

            buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Debug.dbName)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, EnvConfigs.Debug.crashlyticsEnable)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, EnvConfigs.Debug.analyticsEnable)
        }
    }

    kotlinOptions {
        languageVersion = AppConfig.kotlinVersion
        jvmTarget = AppConfig.jvmTarget.target
        freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
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
    // Test
    addTestDependencies()
    TEST
}
