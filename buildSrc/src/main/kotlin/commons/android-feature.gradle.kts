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
