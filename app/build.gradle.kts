import commons.addDefaultConfig
import extensions.addCommonDependencies
import extensions.addComposeDependencies
import extensions.addHiltDependencies
import extensions.addNavigationDependencies
import extensions.addNetworkDependencies
import extensions.addStorageDependencies
import extensions.addWorkManagerDependencies
import extensions.implementation

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.compose")
    id("codeanalyzetools.quality")
    id("codeanalyzetools.jacoco-report")
    id("codeanalyzetools.spotless")
    id("org.jetbrains.kotlin.plugin.serialization") version(Versions.kotlinSerialization)
}

configurations.forEach {
    it.exclude("ui-text-google-fonts")
}

android {
    namespace = AppConfig.namespace
    defaultConfig {
        applicationId = AppConfig.applicationId
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlin.compilerOptions {
        jvmTarget.set(AppConfig.jvmTarget)
    }

    lint {
        // setup outputs
        xmlOutput = File("${project.rootDir}/build/reports/lint/lint-results-debug.xml")
        htmlOutput = File("${project.rootDir}/build/reports/lint/lint-results-debug.html")
        textOutput = File("${project.rootDir}/build/reports/lint/lint-results-debug.txt")
    }

    addDefaultConfig()

}

android.applicationVariants.all {
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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(Deps.multidex)

    addCommonDependencies()

    addComposeDependencies()

    addNavigationDependencies()

    addHiltDependencies()

    addNetworkDependencies()

    addStorageDependencies()

    addWorkManagerDependencies()

    // Testing
//    TEST

//    addFirebaseDependencies()
}