package commons

import AppConfig
import EnvConfigs
import extensions.CORE
import extensions.PLATFORM
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

    addLibDefaultConfig()

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

    kotlinOptions {
        languageVersion = AppConfig.kotlinVersion
        jvmTarget = AppConfig.jvmTarget.target
        freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
    }

}


dependencies {
    coreLibraryDesugaring(Deps.OpenTelemetry.desugaring)
    // Common
    addCommonDependencies()
    // compose navigation
    addNavigationDependencies()
    // `:core` — the mandatory architectural floor for every module (design §3, principle 4:
    // "`:core` is the mandatory floor for every module"). Wired here so a feature can never
    // be missing it, and so narrowing an upper module's re-exports never breaks a feature.
    CORE
    // `:platform` — the cross-feature seam (`AppRoutes`, `AppEventBus`, `EntryProviderInstaller`).
    // Every feature contributes navigation through `@IntoSet EntryProviderInstaller` and so needs
    // `:platform` on its compile classpath; wired here rather than per-feature (design §4, §5).
    PLATFORM
    // Test
    TEST
}

/**
 * Architecture guard — cross-feature dependency detection (design §6.2, Konsist K1).
 *
 * A feature module must never declare another `:features:*` module as a
 * dependency; cross-feature communication goes through `:platform`
 * (`AppRoutes` / `AppEventBus`) or Hilt `@IntoSet` multibinding. This guard
 * inspects the `implementation` and `api` configurations after the module's
 * own build script has been evaluated and reports every offending edge.
 *
 * Phase 0 ships this in **warn mode** (`logger.warn`, build still succeeds).
 * Task 11 flips the `logger.warn(...)` call below to `throw GradleException(...)`
 * so the violation fails at configuration time, before Konsist even runs.
 */
afterEvaluate {
    val currentPath = path
    sequenceOf("implementation", "api")
        .mapNotNull { configurations.findByName(it) }
        .forEach { configuration ->
            configuration.dependencies
                .filterIsInstance<ProjectDependency>()
                .filter { dependency ->
                    dependency.path.startsWith(":features:") && dependency.path != currentPath
                }
                .forEach { dependency ->
                    logger.warn(
                        "[arch-guard] {} declares a cross-feature dependency on {} via `{}`. " +
                            "Features must not depend on other features (design §6.2, Konsist K1) — " +
                            "route through :platform (AppRoutes/AppEventBus) or Hilt @IntoSet instead. " +
                            "Warn-only in Phase 0; Task 11 turns this into a build failure.",
                        currentPath,
                        dependency.path,
                        configuration.name,
                    )
                }
        }
}
