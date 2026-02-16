import danhdue.convention.AppConfig
import danhdue.convention.DepsConfig
import danhdue.convention.EnvConfigs
import danhdue.convention.LibraryDeps
import danhdue.convention.addLibDefaultConfig
import danhdue.convention.addComposeConfig
import com.android.build.api.dsl.CommonExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Apply parcelize
pluginManager.apply("kotlin-parcelize")

// Configure Android extension generically (App or Lib)
extensions.configure<CommonExtension<*, *, *, *, *, *>>("android") {
    addLibDefaultConfig()
    addComposeConfig()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(AppConfig.jvmTarget)
        freeCompilerArgs.addAll(EnvConfigs.FreeCoroutineCompilerArgs)
    }
}

val libs: VersionCatalog = the<VersionCatalogsExtension>().named("libs")

// Note: This plugin is a mixin-style convention plugin — it does NOT directly apply the Android plugin
// (com.android.library / com.android.application). It is applied alongside an Android plugin by other
// convention plugins (e.g., feature or application). Because the Android plugin is not applied here,
// the Kotlin DSL does not generate compile-time accessors like implementation(), api(), etc.
// Therefore, we must use add("configName", dep) to resolve configurations by name at runtime.
dependencies {
    // Common
    add(DepsConfig.API.configName, libs.findLibrary(LibraryDeps.TIMBER).get())
    add(DepsConfig.API.configName, libs.findLibrary(LibraryDeps.ANDROIDX_CORE_KTX).get())
    add(DepsConfig.API.configName, libs.findLibrary(LibraryDeps.ANDROIDX_APPCOMPAT).get())
    add(DepsConfig.API.configName, libs.findLibrary(LibraryDeps.KOTLINX_COROUTINES_CORE).get())
    add(DepsConfig.API.configName, libs.findLibrary(LibraryDeps.KOTLINX_COROUTINES_ANDROID).get())
    add(DepsConfig.API.configName, libs.findLibrary(LibraryDeps.ANDROIDX_LIFECYCLE_RUNTIME_KTX).get())
    add(DepsConfig.API.configName, libs.findLibrary(LibraryDeps.ANDROIDX_LIFECYCLE_VIEWMODEL_KTX).get())
    add(DepsConfig.API.configName, libs.findLibrary(LibraryDeps.ANDROIDX_ACTIVITY_KTX).get())
    add(DepsConfig.COMPILE_ONLY.configName, libs.findLibrary(LibraryDeps.LOMBOK).get())
    add(DepsConfig.ANNOTATION_PROCESSOR.configName, libs.findLibrary(LibraryDeps.LOMBOK).get())

    // Compose
    val composeBom = platform(libs.findLibrary(LibraryDeps.COMPOSE_BOM).get())
    add(DepsConfig.IMPLEMENTATION.configName, composeBom)
    add(DepsConfig.ANDROID_TEST_IMPLEMENTATION.configName, composeBom)

    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_UI).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_MATERIAL3).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_MATERIAL_ICONS_EXTENDED).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_UI_TOOLING).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_UI_TOOLING_PREVIEW).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_RUNTIME).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_FOUNDATION).get())

    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.HILT_NAVIGATION_COMPOSE).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.LIFECYCLE_RUNTIME_COMPOSE).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.ACTIVITY_COMPOSE).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.LIFECYCLE_VIEWMODEL_COMPOSE).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.CONSTRAINTLAYOUT_COMPOSE).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.LOTTIE_COMPOSE).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.PAGING_RUNTIME).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.PAGING_COMMON).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.PAGING_COMPOSE).get())

    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COIL_COMPOSE).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COIL_NETWORK_OKHTTP).get())
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_UI_GRAPHICS).get())

    // Compose Testing
    add(DepsConfig.DEBUG_IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_UI_TOOLING).get())
    add(DepsConfig.DEBUG_IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_UI_TOOLING_PREVIEW).get())
    add(DepsConfig.DEBUG_IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_UI_TEST_MANIFEST).get())
    add(DepsConfig.ANDROID_TEST_IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.COMPOSE_UI_TEST_JUNIT4).get())

    // ExcelReader
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.POI_OOXML).get())

    // Test
    add(DepsConfig.TEST_IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.JUNIT).get())
}
