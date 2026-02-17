import danhdue.convention.AppConfig
import danhdue.convention.EnvConfigs
import danhdue.convention.LibraryDeps
import danhdue.convention.addLibDefaultConfig
import danhdue.convention.buildBooleanConfigField
import danhdue.convention.buildStringConfigField

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("codeanalyzetools.quality")
    id("codeanalyzetools.jacoco-report")
    id("codeanalyzetools.spotless")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    addLibDefaultConfig()

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false

            buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Production.dbName)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, EnvConfigs.Production.crashlyticsEnable)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, EnvConfigs.Production.analyticsEnable)

            proguardFiles(getDefaultProguardFile(AppConfig.proguardOptimizedFileName), AppConfig.proguardConsumerRules)
        }
        debug {
            isMinifyEnabled = false

            buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Development.dbName)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, EnvConfigs.Development.crashlyticsEnable)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, EnvConfigs.Development.analyticsEnable)
        }
    }

    /*
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(AppConfig.kotlinVersion))
        jvmTarget.set(AppConfig.jvmTarget)
        freeCompilerArgs.addAll(EnvConfigs.FreeCoroutineCompilerArgs)
    }
    */
}

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(AppConfig.kotlinVersion))
        jvmTarget.set(AppConfig.jvmTarget)
        freeCompilerArgs.addAll(EnvConfigs.FreeCoroutineCompilerArgs)
    }
}

val libs = the<org.gradle.api.artifacts.VersionCatalogsExtension>().named("libs")

dependencies {
    // coreLibraryDesugaring(libs.findLibrary(LibraryDeps.DESUGAR_JDK_LIBS).get()) // If defined

    // Common
    implementation(libs.findLibrary(LibraryDeps.TIMBER).get())
    implementation(libs.findLibrary(LibraryDeps.ANDROIDX_CORE_KTX).get())
    implementation(libs.findLibrary(LibraryDeps.ANDROIDX_APPCOMPAT).get())
    implementation(libs.findLibrary(LibraryDeps.COROUTINES_TEST).get())

    // Test
    testImplementation(libs.findLibrary(LibraryDeps.JUNIT).get())
}
