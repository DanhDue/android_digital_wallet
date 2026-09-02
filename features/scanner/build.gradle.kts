/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
import commons.addComposeConfig
import extensions.FRAMEWORK
import extensions.UI_KIT
import extensions.addComposeDependencies
import extensions.addNavigationDependencies
import extensions.implementation
import extensions.testImplementation

// ============================================================================
// TEMPLATE DFM EXAMPLE — the one worked Play Feature Delivery / on-demand
// Dynamic Feature Module in this template (Goal G10, design §4.4; see
// docs/architecture/ARCHITECTURE.md for the nav-mechanism contract).
//
// The dependency direction is INVERTED versus an install-time feature: `:app`
// lists this module in `android.dynamicFeatures` and this module depends on
// `:app` — the host never sees the feature (or its Hilt `@Module`s) at compile
// time. Consequences, and why this build script looks nothing like
// `features/settings/build.gradle.kts`:
//
//  * it applies `com.android.dynamic-feature`, NOT `com.android.library` /
//    `commons.android-feature` (a dynamic-feature module is its own AGP plugin
//    type — the two cannot coexist);
//  * it does NOT apply the Hilt Gradle plugin (unsupported on
//    `com.android.dynamic-feature`) and ships NO Hilt code — its navigation entry
//    is contributed at RUNTIME through `com.danhdue.platform.FeatureEntry` +
//    `src/main/resources/META-INF/services/com.danhdue.platform.FeatureEntry`
//    (loaded by `:shell` via `ServiceLoader` once the split is installed),
//    instead of Hilt `@IntoSet` multibinding;
//  * `ScannerRoot` uses the plain AndroidX `viewModel()` (no `hiltViewModel()`).
//
// scanner is the hand-wired **bottom-nav-tab** DFM exemplar: `ShellViewModel` /
// `ShellScreen` install and mount it directly. `shell/.../navigation/
// OnDemandFeatures.kt` is the registry for DFMs reached from an arbitrary call
// site (not a fixed tab) — `mason make ... --delivery on-demand` features use
// that registry; scanner does not.
//
// Generate more on-demand modules with
// `mason make mvi_feature --name <x> --delivery on-demand`.
// ============================================================================

plugins {
    id(Deps.ANDROID_DYNAMIC_FEATURE_PLUGIN_ID)
    id(Deps.KOTLIN_GRADLE_PLUGIN_ID)
    id(Deps.ANDROID_COMPOSE_PLUGIN_ID)
    id(Deps.KOTLIN_SERIALIZATION) version Versions.kotlinSerialization
    id(Deps.CODE_ANALYZE_TOOLS_QUALITY)
}

android {
    namespace = "com.danhdue.scanner"

    compileSdk = AppConfig.compileSdk
    defaultConfig {
        minSdk = AppConfig.minSdk
    }

    addComposeConfig()

    kotlinOptions {
        languageVersion = AppConfig.kotlinVersion
        jvmTarget = AppConfig.jvmTarget.target
        freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
    }

    sourceSets {
        getByName("main") {
            kotlin.srcDirs("src/main/java", "src/main/kotlin")
            java.srcDirs("src/main/java", "src/main/kotlin")
        }
        getByName("test") {
            kotlin.srcDirs("src/test/java", "src/test/kotlin")
            java.srcDirs("src/test/java", "src/test/kotlin")
        }
    }
}

dependencies {
    // Inverted DFM dependency — the split depends on the host, not vice-versa.
    // `:app` must never declare `implementation(project(":features:scanner"))`.
    implementation(project(":app"))
    implementation(project(":platform"))

    FRAMEWORK
    UI_KIT

    addComposeDependencies()
    addNavigationDependencies()

    implementation(Deps.Kotlin.coroutineCore)

    testImplementation(project(":libraries:testutils"))
}
