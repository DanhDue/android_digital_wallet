package extensions

import Deps
import EnvConfigs
import Modules
import PATH
import com.android.build.api.dsl.BuildType
import org.gradle.api.Action
import org.gradle.api.artifacts.ConfigurablePublishArtifact
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible
import org.gradle.kotlin.dsl.accessors.runtime.addConfiguredDependencyTo
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo
import org.gradle.kotlin.dsl.accessors.runtime.addExternalModuleDependencyTo
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.create

/**
 * Adds a dependency to the 'coreLibraryDesugaring' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.coreLibraryDesugaring(dependencyNotation: Any): Dependency? = add("coreLibraryDesugaring", dependencyNotation)

/**
 * Adds a dependency to the 'compileOnly' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.compileOnly(dependencyNotation: Any): Dependency? = add("compileOnly", dependencyNotation)

/**
 * Adds a dependency to the 'compileOnly' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @param dependencyConfiguration expression to use to configure the dependency.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.compileOnly(
    dependencyNotation: String,
    dependencyConfiguration: Action<ExternalModuleDependency>,
): ExternalModuleDependency =
    addDependencyTo(
        this,
        "compileOnly",
        dependencyNotation,
        dependencyConfiguration,
    )

/**
 * Adds a dependency to the 'compileOnly' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @param dependencyConfiguration expression to use to configure the dependency.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.compileOnly(
    dependencyNotation: Provider<*>,
    dependencyConfiguration: Action<ExternalModuleDependency>,
): Unit =
    addConfiguredDependencyTo(
        this,
        "compileOnly",
        dependencyNotation,
        dependencyConfiguration,
    )

/**
 * Adds a dependency to the 'compileOnly' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @param dependencyConfiguration expression to use to configure the dependency.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.compileOnly(
    dependencyNotation: ProviderConvertible<*>,
    dependencyConfiguration: Action<ExternalModuleDependency>,
): Unit =
    addConfiguredDependencyTo(
        this,
        "compileOnly",
        dependencyNotation,
        dependencyConfiguration,
    )

/**
 * Adds a dependency to the 'compileOnly' configuration.
 *
 * @param group the group of the module to be added as a dependency.
 * @param name the name of the module to be added as a dependency.
 * @param version the optional version of the module to be added as a dependency.
 * @param configuration the optional configuration of the module to be added as a dependency.
 * @param classifier the optional classifier of the module artifact to be added as a dependency.
 * @param ext the optional extension of the module artifact to be added as a dependency.
 * @param dependencyConfiguration expression to use to configure the dependency.
 * @return The dependency.
 *
 * @see [DependencyHandler.create]
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.compileOnly(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    dependencyConfiguration: Action<ExternalModuleDependency>? = null,
): ExternalModuleDependency =
    addExternalModuleDependencyTo(
        this,
        "compileOnly",
        group,
        name,
        version,
        configuration,
        classifier,
        ext,
        dependencyConfiguration,
    )

/**
 * Adds a dependency to the 'compileOnly' configuration.
 *
 * @param dependency dependency to be added.
 * @param dependencyConfiguration expression to use to configure the dependency.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun <T : ModuleDependency> DependencyHandler.compileOnly(
    dependency: T,
    dependencyConfiguration: T.() -> Unit,
): T = add("compileOnly", dependency, dependencyConfiguration)

/**
 * Adds a dependency constraint to the 'compileOnly' configuration.
 *
 * @param constraintNotation the dependency constraint notation
 *
 * @return the added dependency constraint
 *
 * @see [DependencyConstraintHandler.add]
 */
fun DependencyConstraintHandler.compileOnly(constraintNotation: Any): DependencyConstraint = add("compileOnly", constraintNotation)

/**
 * Adds a dependency constraint to the 'compileOnly' configuration.
 *
 * @param constraintNotation the dependency constraint notation
 * @param block the block to use to configure the dependency constraint
 *
 * @return the added dependency constraint
 *
 * @see [DependencyConstraintHandler.add]
 */
fun DependencyConstraintHandler.compileOnly(
    constraintNotation: Any,
    block: DependencyConstraint.() -> Unit,
): DependencyConstraint = add("compileOnly", constraintNotation, block)

/**
 * Adds an artifact to the 'compileOnly' configuration.
 *
 * @param artifactNotation the group of the module to be added as a dependency.
 * @return The artifact.
 *
 * @see [ArtifactHandler.add]
 */
fun ArtifactHandler.compileOnly(artifactNotation: Any): PublishArtifact = add("compileOnly", artifactNotation)

/**
 * Adds an artifact to the 'compileOnly' configuration.
 *
 * @param artifactNotation the group of the module to be added as a dependency.
 * @param configureAction The action to execute to configure the artifact.
 * @return The artifact.
 *
 * @see [ArtifactHandler.add]
 */
fun ArtifactHandler.compileOnly(
    artifactNotation: Any,
    configureAction: ConfigurablePublishArtifact.() -> Unit,
): PublishArtifact = add("compileOnly", artifactNotation, configureAction)

/**
 * Adds a dependency to the 'annotationProcessor' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
internal fun DependencyHandler.annotationProcessor(dependencyNotation: Any): Dependency? = add("annotationProcessor", dependencyNotation)

/**
 * Adds a dependency to the `releaseImplementation` configuration.
 *
 * @param dependencyNotation name of dependency to add at specific configuration
 *
 * @return the dependency
 */
fun DependencyHandler.releaseImplementation(dependencyNotation: Any): Dependency? = add("releaseImplementation", dependencyNotation)

/**
 * Adds a dependency to the `debugImplementation` configuration.
 *
 * @param dependencyNotation name of dependency to add at specific configuration
 *
 * @return the dependency
 */
fun DependencyHandler.debugImplementation(dependencyNotation: Any): Dependency? = add("debugImplementation", dependencyNotation)

/**
 * Adds a dependency to the `implementation` configuration.
 *
 * @param dependencyNotation name of dependency to add at specific configuration
 *
 * @return the dependency
 */
fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? = add("implementation", dependencyNotation)

/**
 * Adds a dependency to the `api` configuration.
 *
 * @param dependencyNotation name of dependency to add at specific configuration
 *
 * @return the dependency
 */
fun DependencyHandler.api(dependencyNotation: Any): Dependency? = add("api", dependencyNotation)

/**
 * Adds a dependency to the `kapt` configuration.
 *
 * @param dependencyNotation name of dependency to add at specific configuration
 *
 * @return the dependency
 */
fun DependencyHandler.kapt(dependencyNotation: Any): Dependency? = add("kapt", dependencyNotation)

/**
 * Adds a dependency to the 'kaptTest' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.kaptTest(dependencyNotation: Any): Dependency? = add("kaptTest", dependencyNotation)

/**
 * Adds a dependency to the 'kaptAndroidTest' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.kaptAndroidTest(dependencyNotation: Any): Dependency? = add("kaptAndroidTest", dependencyNotation)

/**
 * Adds a dependency to the `testImplementation` configuration.
 *
 * @param dependencyNotation name of dependency to add at specific configuration
 *
 * @return the dependency
 */
fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? = add("testImplementation", dependencyNotation)

/**
 * Adds a dependency to the `androidTestImplementation` configuration.
 *
 * @param dependencyNotation name of dependency to add at specific configuration
 *
 * @return the dependency
 */
fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? = add("androidTestImplementation", dependencyNotation)

/**
 * Adds a dependency to the `ksp` configuration.
 *
 * @param dependencyNotation name of dependency to add at specific configuration
 *
 * @return the dependency
 */
fun DependencyHandler.ksp(dependencyNotation: Any): Dependency? = add("ksp", dependencyNotation)

/**
 * Adds a dependency to the 'kspTest' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.kspTest(dependencyNotation: Any): Dependency? = add("kspTest", dependencyNotation)

/**
 * Adds a dependency to the 'kspAndroidTest' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.kspAndroidTest(dependencyNotation: Any): Dependency? = add("kspAndroidTest", dependencyNotation)

fun BuildType.addDebugBuildTypeConfigs() {
    enableUnitTestCoverage = false
    enableAndroidTestCoverage = false
    manifestPlaceholders[EnvConfigs.BuildConfigKey.crashlyticsEnableKey] = false
    manifestPlaceholders[EnvConfigs.BuildConfigKey.analyticsEnableKey] = false
}

fun BuildType.addReleaseBuildTypeConfigs() {
    isMinifyEnabled = true
    isShrinkResources = true
    enableUnitTestCoverage = false
    enableAndroidTestCoverage = false
    manifestPlaceholders[EnvConfigs.BuildConfigKey.crashlyticsEnableKey] = true
    manifestPlaceholders[EnvConfigs.BuildConfigKey.analyticsEnableKey] = true
}

fun DependencyHandler.addCommonDependencies() {
    api(Deps.timber)
    api(Deps.coreKtx)
    api(Deps.Appcompat.appCompat)
    api(Deps.Kotlin.coroutineCore)
    api(Deps.Kotlin.coroutine)
    api(Deps.AndroidX.lifecycleRuntimeKtx)
    api(Deps.AndroidX.lifecycleViewmodelKtx)
    api(Deps.Appcompat.activityKtx)
    compileOnly(Deps.LOMBOK)
    annotationProcessor(Deps.LOMBOK)
}

fun DependencyHandler.addNetworkDependencies() {
    // The HTTP stack now lives in `:network` (epic android_super_app_template, design §4.1):
    // NetworkCoreModule (the Retrofit/OkHttp Hilt graph), the interceptors, `apiCall` /
    // `Failure`, and the Flipper network tooling. A consumer that calls this helper gets the
    // module plus its transitively-exposed Retrofit / OkHttp / Moshi libs.
    implementation(project(mapOf(PATH to Modules.network)))
    addJsonParsingDependencies()
    // Retrofit
    implementation(Deps.Networking.retrofit)
    implementation(Deps.Networking.retrofitMoshiConverter)
    // okhttp
    addOkhttpDependencies()
    // chucker
    debugImplementation(Deps.Networking.chuckerDebug)
    releaseImplementation(Deps.Networking.chuckerRelease)
}

fun DependencyHandler.addJsonParsingDependencies() {
    implementation(Deps.Moshi.core)
    ksp(Deps.Moshi.codeGen)
    implementation(Deps.Moshi.lazyAdapter)
}

fun DependencyHandler.addOkhttpDependencies() {
    implementation(platform(Deps.Okhttp.bom))
    implementation(Deps.Okhttp.core)
    implementation(Deps.Okhttp.loggingInterceptor)
    testImplementation(Deps.Okhttp.mockwebserver)
}

fun DependencyHandler.addTestDependencies() {
    api(Deps.Test.junit)
    api(Deps.Test.junitExt)
    api(Deps.Test.junitKtx)
    api(Deps.Kotlin.coroutineTest)
    api(Deps.Test.mockk)
}

fun DependencyHandler.addStorageDependencies() {
    addRoomDependencies()
    implementation(Deps.Storage.dataStorePref)
    implementation(Deps.Storage.dataStore)
    implementation(Deps.Storage.securePref)
    implementation(Deps.Storage.tink)
}

fun DependencyHandler.addRoomDependencies() {
    implementation(Deps.Storage.roomKtx)
    ksp(Deps.Storage.roomCompiler)
}

fun DependencyHandler.addHiltDependencies() {
    implementation(Deps.Hilt.core)
    ksp(Deps.Hilt.compiler)
    androidTestImplementation(Deps.Hilt.testing)
    kspAndroidTest(Deps.Hilt.compiler)
    testImplementation(Deps.Hilt.testing)
}

fun DependencyHandler.addComposeDependencies() {
    implementation(platform(Deps.Compose.composeBOM))

    implementation(Deps.Compose.composeUI)
    implementation(Deps.Compose.material3)
    implementation(Deps.Compose.uiTooling)
    implementation(Deps.Compose.uiToolingPreview)
    implementation(Deps.Compose.runtime)
    implementation(Deps.Compose.foundation)
    implementation(Deps.Compose.iconsCore)
    implementation(Deps.Compose.iconsExtended)

    // hilt compose navigation
    implementation(Deps.Hilt.navigationCompose)

    implementation(Deps.Compose.activityCompose)
    implementation(Deps.Compose.lifecycleViewmodelCompose)
    implementation(Deps.Compose.constraintLayout)
    implementation(Deps.Compose.lottieCompose)
    implementation(Deps.AndroidX.paging)
    implementation(Deps.Compose.pagingCompose)
    implementation(Deps.Compose.coilCompose)
    implementation(Deps.Compose.coilNetworkOkhttp)
    implementation(Deps.Compose.composeUIGraphics)

    // Compose Testing
    testImplementation(Deps.AndroidX.pagingCommon)
    androidTestImplementation(platform(Deps.Compose.composeBOM))
    debugImplementation(Deps.Test.uiTooling)
    debugImplementation(Deps.Test.uiTestManifest)
    androidTestImplementation(Deps.Test.uiTestJunit4)

    // ExcelReader
    implementation(Deps.ExcelReader.excelReader)
}

fun DependencyHandler.addWorkManagerDependencies() {
    implementation(Deps.WorkManager.workRuntimeKtx)
    implementation(Deps.WorkManager.workMultiProcess)
    implementation(Deps.WorkManager.workHilt)
    androidTestImplementation(Deps.WorkManager.workTesting)
}

fun DependencyHandler.addNavigationDependencies() {
    implementation(Deps.Navigation.nav3Ui)
    implementation(Deps.Navigation.nav3Runtime)
    implementation(Deps.Navigation.nav3ViewModel)
    implementation(Deps.Navigation.nav3Adaptive)
    implementation(Deps.Navigation.nav3SerializationCore)
    implementation(Deps.Navigation.navigationCommonKtx)
}

fun DependencyHandler.addFirebaseDependencies() {
    implementation(platform(Deps.Firebase.bom))
    implementation(Deps.Firebase.analytics)
    implementation(Deps.Firebase.crashlytics)
    implementation(Deps.Firebase.remoteConfig)
}

fun DependencyHandler.addFlipperDependencies() {
    debugImplementation(Deps.Flipper.core)
    debugImplementation(Deps.Flipper.network)
    debugImplementation(Deps.Flipper.soLoader)
    releaseImplementation(Deps.Flipper.noOp)
}

fun DependencyHandler.addLeakCanaryDependencies() {
    debugImplementation(Deps.LeakCanary.android)
    debugImplementation(Deps.FlipperPlugins.leakCanary)
}

fun DependencyHandler.addJacksonMsgPackDependencies() {
    implementation(Deps.Jackson.core)
    implementation(Deps.Jackson.annotations)
    implementation(Deps.Jackson.databinding)
    implementation(Deps.Jackson.jacksonKotlin)
    implementation(Deps.Jackson.jacksonMsgPackDataFormat)
}

fun DependencyHandler.addModuleDependencies() {
    implementation(project(mapOf(PATH to Modules.uiKit)))

    implementation(project(mapOf(PATH to Modules.librariesFramework)))

    implementation(project(mapOf(PATH to Modules.librariesTestUtils)))

    implementation(project(mapOf(PATH to Modules.dataModel)))
    implementation(project(mapOf(PATH to Modules.dataLocal)))
    implementation(project(mapOf(PATH to Modules.dataRemote)))
    implementation(project(mapOf(PATH to Modules.dataRepository)))

    implementation(project(mapOf(PATH to Modules.featureSplash)))
    implementation(project(mapOf(PATH to Modules.featureHome)))
    implementation(project(mapOf(PATH to Modules.featureSettings)))
}

fun DependencyHandler.addOpenTelemetryDependencies() {
    implementation(platform(Deps.OpenTelemetry.otelBom))
    implementation(Deps.OpenTelemetry.otelApi)
    implementation(Deps.OpenTelemetry.otelKtx)
    implementation(Deps.OpenTelemetry.otelSdk)
    implementation(Deps.OpenTelemetry.otelContext)
    implementation(Deps.OpenTelemetry.otelExporterOtlp)
    implementation(Deps.OpenTelemetry.otelExporterLogging)
    implementation(Deps.OpenTelemetry.otelSemConv)
    implementation(Deps.OpenTelemetry.otelTracingShim)
    implementation(Deps.OpenTelemetry.otelPropagators)
    implementation(Deps.OpenTelemetry.otelGrpcProtobuf)
    implementation(Deps.OpenTelemetry.otelGrpcStub)
//    implementation(Deps.OpenTelemetry.otelGrpcNettyShaded)
    implementation(Deps.OpenTelemetry.otelOkhttp3)
    // Open Telemetry Android
//    implementation(Deps.OpenTelemetry.otelAndroid)
//    implementation(Deps.OpenTelemetry.otelAndroidCommon)
//    implementation(Deps.OpenTelemetry.otelAndroidInstrumentationApi)
//    implementation(Deps.OpenTelemetry.otelAndroidInstrumentationStartup)
//    implementation(Deps.OpenTelemetry.otelAndroidInstrumentationLifecycle)
//    implementation(Deps.OpenTelemetry.otelAndroidInstrumentationActivity)
//    implementation(Deps.OpenTelemetry.otelAndroidInstrumentationAnr)
//    implementation(Deps.OpenTelemetry.otelAndroidInstrumentationCrash)
//    implementation(Deps.OpenTelemetry.otelAndroidInstrumentationFragment)
//    implementation(Deps.OpenTelemetry.otelAndroidInstrumentationNetwork)
//    implementation(Deps.OpenTelemetry.otelAndroidOkhttpAgent)
//    implementation(Deps.OpenTelemetry.otelAndroidOkhttpLibrary)
//    implementation(Deps.OpenTelemetry.otelAndroidInstrumentationSlowrendering)
}

// Modules
val DependencyHandler.CORE
    get() = implementation(project(mapOf(PATH to Modules.core)))

val DependencyHandler.NETWORK
    get() = implementation(project(mapOf(PATH to Modules.network)))

val DependencyHandler.PLATFORM
    get() = implementation(project(mapOf(PATH to Modules.platform)))

val DependencyHandler.MODEL
    get() = implementation(project(mapOf(PATH to Modules.dataModel)))

val DependencyHandler.LOCAL
    get() = implementation(project(mapOf(PATH to Modules.dataLocal)))

val DependencyHandler.REMOTE
    get() = implementation(project(mapOf(PATH to Modules.dataRemote)))

val DependencyHandler.REPOSITORY
    get() = implementation(project(mapOf(PATH to Modules.dataRepository)))

val DependencyHandler.UI_KIT
    get() = implementation(project(mapOf(PATH to Modules.uiKit)))

val DependencyHandler.FRAMEWORK
    get() = implementation(project(mapOf(PATH to Modules.librariesFramework)))

val DependencyHandler.TEST
    get() = testImplementation(project(mapOf(PATH to Modules.librariesTestUtils)))

val DependencyHandler.FEATURE_SPLASH
    get() = implementation(project(mapOf(PATH to Modules.featureSplash)))

val DependencyHandler.FEATURE_HOME
    get() = implementation(project(mapOf(PATH to Modules.featureHome)))

val DependencyHandler.FEATURE_SETTINGS
    get() = implementation(project(mapOf(PATH to Modules.featureSettings)))

val DependencyHandler.FEATURE_AUTHENTICATION
    get() = implementation(project(mapOf(PATH to Modules.featureAuthentication)))

val DependencyHandler.FEATURE_MY_WALLET
    get() = implementation(project(mapOf(PATH to Modules.featureMyWallet)))

val DependencyHandler.FEATURE_TRANSACTIONS
    get() = implementation(project(mapOf(PATH to Modules.featureTransactions)))

val DependencyHandler.FEATURE_SCANNER
    get() = implementation(project(mapOf(PATH to Modules.featureScanner)))

val DependencyHandler.FEATURE_TRENDS
    get() = implementation(project(mapOf(PATH to Modules.featureTrends)))
