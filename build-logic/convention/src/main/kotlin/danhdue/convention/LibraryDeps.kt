package danhdue.convention

/**
 * Central registry of version catalog library alias strings.
 * Used by convention plugins to avoid duplicated string literals in `libs.findLibrary(...)` calls.
 */
object LibraryDeps {

    // region Common
    const val TIMBER = "timber"
    const val ANDROIDX_CORE_KTX = "androidx-core-ktx"
    const val ANDROIDX_APPCOMPAT = "androidx-appcompat"
    const val KOTLINX_COROUTINES_CORE = "kotlinx-coroutines-core"
    const val KOTLINX_COROUTINES_ANDROID = "kotlinx-coroutines-android"
    const val ANDROIDX_LIFECYCLE_RUNTIME_KTX = "androidx-lifecycle-runtime-ktx"
    const val ANDROIDX_LIFECYCLE_VIEWMODEL_KTX = "androidx-lifecycle-viewmodel-ktx"
    const val ANDROIDX_ACTIVITY_KTX = "androidx-activity-ktx"
    const val LOMBOK = "lombok"
    const val DESUGAR_JDK_LIBS = "desugar-jdk-libs"
    // endregion

    // region Hilt
    const val HILT_ANDROID = "hilt-android"
    const val HILT_COMPILER = "hilt-compiler"
    const val HILT_TESTING = "hilt-testing"
    const val HILT_NAVIGATION_COMPOSE = "hilt-navigation-compose"
    // endregion

    // region Compose
    const val COMPOSE_BOM = "androidx-compose-bom"
    const val COMPOSE_UI = "androidx-compose-ui"
    const val COMPOSE_MATERIAL3 = "androidx-compose-material3"
    const val COMPOSE_MATERIAL_ICONS_EXTENDED = "androidx-compose-material-icons-extended"
    const val COMPOSE_UI_TOOLING = "androidx-compose-ui-tooling"
    const val COMPOSE_UI_TOOLING_PREVIEW = "androidx-compose-ui-tooling-preview"
    const val COMPOSE_RUNTIME = "androidx-compose-runtime"
    const val COMPOSE_FOUNDATION = "androidx-compose-foundation"
    const val COMPOSE_UI_GRAPHICS = "androidx-compose-ui-graphics"
    const val COMPOSE_UI_TEST_MANIFEST = "androidx-compose-ui-test-manifest"
    const val COMPOSE_UI_TEST_JUNIT4 = "androidx-compose-ui-test-junit4"
    const val LIFECYCLE_RUNTIME_COMPOSE = "androidx-lifecycle-runtime-compose"
    const val ACTIVITY_COMPOSE = "androidx-activity-compose"
    const val LIFECYCLE_VIEWMODEL_COMPOSE = "androidx-lifecycle-viewmodel-compose"
    const val CONSTRAINTLAYOUT_COMPOSE = "androidx-constraintlayout-compose"
    const val LOTTIE_COMPOSE = "lottie-compose"
    // endregion

    // region Paging
    const val PAGING_RUNTIME = "androidx-paging-runtime"
    const val PAGING_COMMON = "androidx-paging-common"
    const val PAGING_COMPOSE = "androidx-paging-compose"
    // endregion

    // region Image Loading
    const val COIL_COMPOSE = "coil-compose"
    const val COIL_NETWORK_OKHTTP = "coil-network-okhttp"
    // endregion

    // region Navigation
    const val NAVIGATION3_RUNTIME = "androidx-navigation3-runtime"
    const val NAVIGATION3_UI = "androidx-navigation3-ui"
    const val LIFECYCLE_VIEWMODEL_NAVIGATION3 = "androidx-lifecycle-viewmodel-navigation3"
    const val KOTLINX_SERIALIZATION_CORE = "kotlinx-serialization-core"
    const val NAVIGATION_COMMON_KTX = "androidx-navigation-common-ktx"
    // endregion

    // region Testing
    const val JUNIT = "junit"
    const val ANDROIDX_JUNIT = "androidx-junit"
    const val ESPRESSO_CORE = "androidx-espresso-core"
    const val COROUTINES_TEST = "coroutines-test"
    const val MOCKK = "mockk"
    // endregion

    // region Other
    const val POI_OOXML = "poi-ooxml"
    // endregion
}

/**
 * Gradle dependency configuration names used in convention plugins.
 * Provides type-safe access to configuration strings for `dependencies { add(...) }` calls.
 */
enum class DepsConfig(val configName: String) {
    IMPLEMENTATION("implementation"),
    API("api"),
    COMPILE_ONLY("compileOnly"),
    ANNOTATION_PROCESSOR("annotationProcessor"),
    KSP("ksp"),
    KSP_ANDROID_TEST("kspAndroidTest"),
    ANDROID_TEST_IMPLEMENTATION("androidTestImplementation"),
    TEST_IMPLEMENTATION("testImplementation"),
    DEBUG_IMPLEMENTATION("debugImplementation"),
    CORE_LIBRARY_DESUGARING("coreLibraryDesugaring"),
}
