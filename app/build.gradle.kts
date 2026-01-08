
import commons.addDefaultConfig
import extensions.FEATURE_AUTHENTICATION
import extensions.FEATURE_HOME
import extensions.FEATURE_MY_WALLET
import extensions.FEATURE_SCANNER
import extensions.FEATURE_SETTINGS
import extensions.FEATURE_TRANSACTIONS
import extensions.FEATURE_TRENDS
import extensions.FRAMEWORK
import extensions.addCommonDependencies
import extensions.addComposeDependencies
import extensions.addHiltDependencies
import extensions.addNavigationDependencies
import extensions.addNetworkDependencies
import extensions.addStorageDependencies
import extensions.addWorkManagerDependencies
import extensions.implementation

plugins {
    id(Deps.ANDROID_GRADLE_PLUGIN_ID)
    id(Deps.KOTLIN_GRADLE_PLUGIN_ID)
    id(Deps.KOTLIN_SYMBOL_PROCESSING_PLUGIN_ID)
    id(Deps.ANDROID_HILT_PLUGIN_ID)
    id(Deps.KOTLIN_PARCELIZE)
    id(Deps.ANDROID_COMPOSE_PLUGIN_ID)
    id(Deps.CODE_ANALYZE_TOOLS_QUALITY)
    id(Deps.CODE_ANALYZE_TOOLS_JACOCO)
    id(Deps.CODE_ANALYZE_TOOLS_SPOTLESS)
    id(Deps.KOTLIN_SERIALIZATION) version (Versions.kotlinSerialization)
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
                "proguard-rules.pro",
            )
        }
    }

    kotlinOptions {
        languageVersion = AppConfig.kotlinVersion
        jvmTarget = AppConfig.jvmTarget.target
        freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
    }

    lint {
        // setup outputs
        xmlOutput = File("${project.rootDir}/build/reports/lint/lint-results-debug.xml")
        htmlOutput = File("${project.rootDir}/build/reports/lint/lint-results-debug.html")
        textOutput = File("${project.rootDir}/build/reports/lint/lint-results-debug.txt")
    }

    addDefaultConfig()
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

    FRAMEWORK
    FEATURE_AUTHENTICATION
    FEATURE_HOME
    FEATURE_MY_WALLET
    FEATURE_TRANSACTIONS
    FEATURE_SCANNER
    FEATURE_TRENDS
    FEATURE_SETTINGS

    // Testing
//    TEST

//    addFirebaseDependencies()
}
