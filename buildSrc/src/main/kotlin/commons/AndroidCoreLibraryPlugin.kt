package commons

import AppConfig
import EnvConfigs
import com.android.build.gradle.LibraryExtension
import extensions.buildBooleanConfigField
import extensions.buildStringConfigField
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidCoreLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.applyPlugins()
        target.configureAndroid()
        target.configureKotlin()
    }

    private fun Project.applyPlugins() {
        plugins.apply("com.android.library")
        plugins.apply("org.jetbrains.kotlin.android")
        plugins.apply("org.jetbrains.kotlin.plugin.parcelize")
        plugins.apply("com.google.devtools.ksp")
    }

    private fun Project.configureKotlin() =
        extensions.getByType(KotlinAndroidProjectExtension::class).run {
            sourceSets.apply {
                getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin")
                getByName("test").kotlin.srcDir("build/generated/ksp/test/kotlin")
                getByName("debug").kotlin.srcDir("build/generated/ksp/debug/kotlin")
                getByName("release").kotlin.srcDir("build/generated/ksp/release/kotlin")
            }
        }

    private fun Project.configureAndroid() = extensions.getByType(LibraryExtension::class).run {
        compileSdk = AppConfig.compileSdk
        defaultConfig.apply {
            minSdk = AppConfig.minSdk
            targetSdk = AppConfig.targetSdk
            testInstrumentationRunner = AppConfig.androidTestInstrumentation
        }

        compileOptions.apply {
            sourceCompatibility = AppConfig.sourceCompatibility
            targetCompatibility = AppConfig.targetCompatibility
        }

        project.tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                languageVersion = AppConfig.kotlinVersion
                apiVersion = AppConfig.kotlinVersion
                jvmTarget = AppConfig.jvmTarget
                freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
            }
        }

        buildTypes.apply {
            getByName("release") {
                proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")

                buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Release.dbName)
                buildBooleanConfigField(
                    EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE,
                    EnvConfigs.Release.crashlyticsEnable,
                )
                buildBooleanConfigField(
                    EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE,
                    EnvConfigs.Release.analyticsEnable,
                )
            }
            getByName("debug") {
                proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")

                buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Debug.dbName)
                buildBooleanConfigField(
                    EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE,
                    EnvConfigs.Debug.crashlyticsEnable,
                )
                buildBooleanConfigField(
                    EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE,
                    EnvConfigs.Debug.analyticsEnable,
                )
            }
        }
    }
}
