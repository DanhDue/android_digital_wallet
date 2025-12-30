package commons

import AppConfig
import EnvConfigs
import extensions.TEST
import extensions.addCommonDependencies
import extensions.addTestDependencies
import extensions.buildBooleanConfigField
import extensions.buildStringConfigField

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("codeanalyzetools.quality")
    id("codeanalyzetools.jacoco-report")
    id("codeanalyzetools.spotless")
    id("variant.library-variant")
}

android {
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        testInstrumentationRunner = AppConfig.androidTestInstrumentation
    }

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

    compileOptions {
        sourceCompatibility = AppConfig.sourceCompatibility
        targetCompatibility = AppConfig.targetCompatibility
    }

    buildFeatures {
        buildConfig = true
    }

    kotlinOptions {
        languageVersion = AppConfig.kotlinVersion
        apiVersion = AppConfig.kotlinVersion
        jvmTarget = AppConfig.jvmTarget
        freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    packaging {
        resources.excludes.apply {
            add("META-INF/AL2.0")
            add("META-INF/LGPL2.1")
            add("/META-INF/{AL2.0,LGPL2.1}")
            add("META-INF/DEPENDENCIES")
            add("META-INF/LICENSE")
            add("META-INF/LICENSE.txt")
            add("META-INF/license.txt")
            add("META-INF/LICENSE.md")
            add("META-INF/LICENSE-notice.md")
            add("META-INF/NOTICE")
            add("META-INF/NOTICE.txt")
            add("META-INF/notice.txt")
            add("META-INF/ASL2.0")
            add("META-INF/*.kotlin_module")
            add("META-INF/*")
            add("META-INF/gradle/incremental.annotation.processors")
            add("/META-INF/{AL2.0,LGPL2.1,gradle-plugins}")
            jniLibs.pickFirsts.add("**/*.so")
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
    // Test
    addTestDependencies()
    TEST
}
