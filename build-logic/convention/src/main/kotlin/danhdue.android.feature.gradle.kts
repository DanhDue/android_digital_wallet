import danhdue.convention.AppConfig
import danhdue.convention.DepsConfig
import danhdue.convention.EnvConfigs
import danhdue.convention.LibraryDeps
import danhdue.convention.buildBooleanConfigField
import danhdue.convention.buildStringConfigField
import danhdue.convention.addLibDefaultConfig

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("codeanalyzetools.quality")
    id("codeanalyzetools.jacoco-report")
    id("codeanalyzetools.spotless")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {

    addLibDefaultConfig()

    buildTypes {
        release {
            buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Production.dbName)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, EnvConfigs.Production.crashlyticsEnable)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, EnvConfigs.Production.analyticsEnable)
        }

        debug {
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

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    add(DepsConfig.CORE_LIBRARY_DESUGARING.configName, libs.findLibrary(LibraryDeps.DESUGAR_JDK_LIBS).get())

    // Common
    api(libs.findLibrary(LibraryDeps.TIMBER).get())
    api(libs.findLibrary(LibraryDeps.ANDROIDX_CORE_KTX).get())
    api(libs.findLibrary(LibraryDeps.ANDROIDX_APPCOMPAT).get())
    api(libs.findLibrary(LibraryDeps.KOTLINX_COROUTINES_CORE).get())
    api(libs.findLibrary(LibraryDeps.KOTLINX_COROUTINES_ANDROID).get())
    api(libs.findLibrary(LibraryDeps.ANDROIDX_LIFECYCLE_RUNTIME_KTX).get())
    api(libs.findLibrary(LibraryDeps.ANDROIDX_LIFECYCLE_VIEWMODEL_KTX).get())
    api(libs.findLibrary(LibraryDeps.ANDROIDX_ACTIVITY_KTX).get())
    compileOnly(libs.findLibrary(LibraryDeps.LOMBOK).get())
    add(DepsConfig.ANNOTATION_PROCESSOR.configName, libs.findLibrary(LibraryDeps.LOMBOK).get())

    // Hilt
    implementation(libs.findLibrary(LibraryDeps.HILT_ANDROID).get())
    ksp(libs.findLibrary(LibraryDeps.HILT_COMPILER).get())
    androidTestImplementation(libs.findLibrary(LibraryDeps.HILT_TESTING).get())
    add(DepsConfig.KSP_ANDROID_TEST.configName, libs.findLibrary(LibraryDeps.HILT_COMPILER).get())
    testImplementation(libs.findLibrary(LibraryDeps.HILT_TESTING).get())

    // Compose Navigation
    // implementation("androidx.navigation3:navigation3-ui:2.9.0-alpha04")
    implementation(libs.findLibrary(LibraryDeps.NAVIGATION3_RUNTIME).get())
    implementation(libs.findLibrary(LibraryDeps.NAVIGATION3_UI).get())
    implementation(libs.findLibrary(LibraryDeps.LIFECYCLE_VIEWMODEL_NAVIGATION3).get())
    // implementation("androidx.compose.material3.adaptive:adaptive-navigation3:1.3.0-alpha05")
    implementation(libs.findLibrary(LibraryDeps.KOTLINX_SERIALIZATION_CORE).get())
    implementation(libs.findLibrary(LibraryDeps.NAVIGATION_COMMON_KTX).get())

    // Test
    api(libs.findLibrary(LibraryDeps.JUNIT).get())
    api(libs.findLibrary(LibraryDeps.ANDROIDX_JUNIT).get())
    api(libs.findLibrary(LibraryDeps.ESPRESSO_CORE).get())
    api(libs.findLibrary(LibraryDeps.COROUTINES_TEST).get())
    api(libs.findLibrary(LibraryDeps.MOCKK).get())
}
