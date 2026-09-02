
import commons.addDefaultConfig
import extensions.FEATURE_SCANNER
import extensions.FEATURE_SETTINGS
import extensions.CORE
import extensions.FRAMEWORK
import extensions.NETWORK
import extensions.PLATFORM
import extensions.SHELL
import extensions.UI_KIT
import extensions.addCommonDependencies
import extensions.addComposeDependencies
import extensions.addHiltDependencies
import extensions.addLeakCanaryDependencies
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

    // Required for Flipper native libs on Android 15+ (16KB page alignment)
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
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

    addLeakCanaryDependencies()

    // Infrastructure modules — declared explicitly (Task 9 narrowed `:framework`'s re-exports).
    // `:app` imports `com.danhdue.core.*` (AppInitializer, DispatcherProvider),
    // `com.danhdue.platform.*` (EntryProviderInstaller, LocalEntryProviderInstallers) and
    // `com.danhdue.network.*` (FlipperInitializer, NetworkConfig) directly.
    CORE
    NETWORK
    PLATFORM
    UI_KIT
    FRAMEWORK
    // `:shell` — the Host tab shell (relocated from `features/home`, Task 10). Contributes
    // `ShellNavigationModule`'s `@IntoSet EntryProviderInstaller` (`entry<ShellRoute> { ShellRoot() }`)
    // to the `@HiltAndroidApp` root.
    SHELL
    // The template's install-time features stay declared here so Hilt aggregates every feature's
    // `@IntoSet EntryProviderInstaller` at the `@HiltAndroidApp` root (design §4.1, §9 Phase 2).
    // `scanner` becomes a dynamic-feature module in Task 14.
    FEATURE_SCANNER
    FEATURE_SETTINGS

    // Testing
//    TEST

//    addFirebaseDependencies()
}
