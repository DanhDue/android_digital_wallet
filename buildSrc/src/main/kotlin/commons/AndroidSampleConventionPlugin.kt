package commons

import AppConfig
import Deps
import EnvConfigs
import Modules
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Standalone Application Runner convention plugin for Mini Apps (`commons.android-sample`).
 *
 * Configures an isolated application runner module (`:features:<feature>:sample`) with:
 * - Application ID: `"${AppConfig.applicationId}.sample.<feature>"`
 * - Shared Compose + Navigation 3 + Hilt + Platform baseline dependencies.
 * - 16 KB page-aligned native library packaging.
 */
class AndroidSampleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.applyPlugins()
        target.configureAndroid()
        target.configureKotlin()
        target.configureDependencies()
    }

    private fun Project.applyPlugins() {
        pluginManager.apply(Deps.ANDROID_GRADLE_PLUGIN_ID)
        pluginManager.apply(Deps.KOTLIN_GRADLE_PLUGIN_ID)
        pluginManager.apply(Deps.ANDROID_COMPOSE_PLUGIN_ID)
        pluginManager.apply(Deps.KOTLIN_SYMBOL_PROCESSING_PLUGIN_ID)
        pluginManager.apply(Deps.ANDROID_HILT_PLUGIN_ID)
        pluginManager.apply("codeanalyzetools.quality")
        pluginManager.apply("codeanalyzetools.spotless")
    }

    private fun Project.configureAndroid() {
        extensions.configure<ApplicationExtension> {
            val featureName = parent?.name ?: name
            namespace = "com.danhdue.features.$featureName.sample"
            compileSdk = AppConfig.compileSdk

            defaultConfig {
                applicationId = "${AppConfig.applicationId}.sample.$featureName"
                minSdk = AppConfig.minSdk
                targetSdk = AppConfig.targetSdk
                versionCode = 1
                versionName = "1.0.0-sample"
                testInstrumentationRunner = AppConfig.androidTestInstrumentation
                vectorDrawables.useSupportLibrary = true
            }

            compileOptions {
                sourceCompatibility = AppConfig.sourceCompatibility
                targetCompatibility = AppConfig.targetCompatibility
            }

            buildFeatures {
                compose = true
                buildConfig = true
            }

            packaging {
                jniLibs {
                    useLegacyPackaging = false
                    pickFirsts.add("**/*.so")
                }
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
                    add("META-INF/gradle/incremental.annotation.processors")
                    add("/META-INF/{AL2.0,LGPL2.1,gradle-plugins}")
                }
            }
        }
    }

    private fun Project.configureKotlin() {
        extensions.configure<KotlinAndroidProjectExtension> {
            compilerOptions {
                languageVersion.set(KotlinVersion.fromVersion(AppConfig.kotlinVersion))
                apiVersion.set(KotlinVersion.fromVersion(AppConfig.kotlinVersion))
                jvmTarget.set(AppConfig.jvmTarget)
            }
        }

        tasks.withType<KotlinCompilationTask<*>>().configureEach {
            compilerOptions {
                languageVersion.set(KotlinVersion.fromVersion(AppConfig.kotlinVersion))
                apiVersion.set(KotlinVersion.fromVersion(AppConfig.kotlinVersion))
            }
        }

        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(AppConfig.jvmTarget)
                freeCompilerArgs.addAll(EnvConfigs.FreeCoroutineCompilerArgs)
            }
        }
    }

    private fun Project.configureDependencies() {
        dependencies.apply {
            // Packages baseline: core, network, platform, framework, ui_kit
            add("implementation", project(mapOf("path" to Modules.core)))
            add("implementation", project(mapOf("path" to Modules.network)))
            add("implementation", project(mapOf("path" to Modules.platform)))
            add("implementation", project(mapOf("path" to Modules.framework)))
            add("implementation", project(mapOf("path" to Modules.uiKit)))

            // Compose & Lifecycle
            add("implementation", platform(Deps.Compose.composeBOM))
            add("implementation", Deps.Compose.composeUI)
            add("implementation", Deps.Compose.material3)
            add("implementation", Deps.Compose.uiTooling)
            add("implementation", Deps.Compose.uiToolingPreview)
            add("implementation", Deps.Compose.runtime)
            add("implementation", Deps.Compose.foundation)
            add("implementation", Deps.Compose.activityCompose)
            add("implementation", Deps.Compose.lifecycleViewmodelCompose)

            // Navigation 3
            add("implementation", Deps.Navigation.nav3Ui)
            add("implementation", Deps.Navigation.nav3Runtime)
            add("implementation", Deps.Navigation.nav3ViewModel)
            add("implementation", Deps.Navigation.nav3Adaptive)
            add("implementation", Deps.Navigation.nav3SerializationCore)
            add("implementation", Deps.Navigation.navigationCommonKtx)

            // Hilt & Multidex
            add("implementation", Deps.multidex)
            add("implementation", Deps.Hilt.core)
            add("ksp", Deps.Hilt.compiler)
            add("implementation", Deps.Hilt.navigationCompose)

            // Logging
            add("implementation", Deps.timber)
        }
    }
}
