
import commons.addDefaultConfig
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
import extensions.TEST

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

// `:app` is the base module of the on-demand `:features:scanner` Dynamic Feature
// Module (Task 14). A dynamic-feature base APK must not expose any `compileOnly`
// Android dependency, and `addCommonDependencies()` pulls in `compileOnly` Lombok
// that `:app` never uses — drop it from every configuration so the split builds.
configurations.configureEach {
    exclude(group = "org.projectlombok", module = "lombok")
}

android {
    namespace = AppConfig.namespace
    defaultConfig {
        applicationId = AppConfig.applicationId
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
    }

    // On-demand Dynamic Feature Module split (Task 14, design §4.4). The dependency
    // is INVERTED: `:app` declares the module here and NEVER as an
    // `implementation(project(...))`, `:features:scanner` depends on `:app`. The
    // scanner split is absent from the base APK and is installed at runtime by
    // `FeatureInstallerImpl` (`SplitInstallManager` + `SplitCompat`). Konsist K8
    // reads this list to exempt `com.danhdue.scanner.*` from the "no host imports"
    // rule. Add more on-demand modules with `mvi_feature --delivery on-demand`.
    dynamicFeatures += setOf(":features:scanner")

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

    // Play Feature Delivery — installs the on-demand `:features:scanner` split at
    // runtime (Task 14). `SplitInstallManager` / `SplitInstallRequest` live in
    // `feature-delivery`; `feature-delivery-ktx` adds the coroutine helpers.
    implementation(Deps.Play.featureDelivery)
    implementation(Deps.Play.featureDeliveryKtx)

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
    // `scanner` is NOT here: it is an on-demand Dynamic Feature Module (Task 14) declared in
    // `android.dynamicFeatures` above — the dependency is inverted (`:features:scanner` → `:app`).
    FEATURE_SETTINGS

    // Testing — `:libraries:testutils` re-exports junit + robolectric + mockk +
    // coroutines-test + turbine, used by `FeatureInstallerImplTest` (Robolectric,
    // exercises the real `SplitInstallManager`-backed installer).
    TEST

//    addFirebaseDependencies()
}
