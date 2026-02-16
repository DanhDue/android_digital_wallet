import danhdue.convention.DepsConfig
import danhdue.convention.LibraryDeps

plugins {
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

val libs = the<VersionCatalogsExtension>().named("libs")

// Note: This plugin is a mixin-style convention plugin — it does NOT directly apply the Android plugin
// (com.android.library / com.android.application). It is applied alongside an Android plugin by other
// convention plugins (e.g., feature or application). Because the Android plugin is not applied here,
// the Kotlin DSL does not generate compile-time accessors like implementation(), api(), etc.
// Therefore, we must use add("configName", dep) to resolve configurations by name at runtime.
dependencies {
    add(DepsConfig.IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.HILT_ANDROID).get())
    add(DepsConfig.KSP.configName, libs.findLibrary(LibraryDeps.HILT_COMPILER).get())
    add(DepsConfig.ANDROID_TEST_IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.HILT_TESTING).get())
    add(DepsConfig.KSP_ANDROID_TEST.configName, libs.findLibrary(LibraryDeps.HILT_COMPILER).get())
    add(DepsConfig.TEST_IMPLEMENTATION.configName, libs.findLibrary(LibraryDeps.HILT_TESTING).get())
}
