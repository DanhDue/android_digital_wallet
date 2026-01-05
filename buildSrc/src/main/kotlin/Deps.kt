const val PATH = "path"

object Deps {
    const val ANDROID_GRADLE_PLUGIN_ID = "com.android.application"
    const val KOTLIN_GRADLE_PLUGIN_ID = "org.jetbrains.kotlin.android"
    const val KOTLIN_SYMBOL_PROCESSING_PLUGIN_ID  = "com.google.devtools.ksp"
    const val ANDROID_HILT_PLUGIN  = "dagger.hilt.android.plugin"
    const val ANDROID_HILT_PLUGIN_ID  = "com.google.dagger.hilt.android"
    const val KOTLIN_PARCELIZE  = "kotlin-parcelize"
    const val ANDROID_LIBRARY_GRADLE_PLUGIN_ID = "com.android.library"
    const val COMMONS_ANDROID_LIBRARY = "commons.android-library"
    const val COMMONS_DAGGER_HILT = "commons.dagger-hilt"
    const val COMMONS_ANDROID_FEATURE = "commons.android-feature"
    const val COMMONS_ANDROID_COMPOSE = "commons.android-compose"
    const val ANDROID_COMPOSE_PLUGIN_ID = "org.jetbrains.kotlin.plugin.compose"
    const val GOOGLE_SERVICE_GRADLE_PLUGIN_ID = "com.google.gms.google-services"
    const val GOOGLE_CRASHLYTICS_GRADLE_PLUGIN_ID = "com.google.firebase.crashlytics"
    const val CODE_ANALYZE_TOOLS_QUALITY = "codeanalyzetools.quality"
    const val CODE_ANALYZE_TOOLS_JACOCO = "codeanalyzetools.jacoco-report"
    const val CODE_ANALYZE_TOOLS_SPOTLESS = "codeanalyzetools.spotless"
    const val KOTLIN_SERIALIZATION = "org.jetbrains.kotlin.plugin.serialization"
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val multidex = "androidx.multidex:multidex:${Versions.multidex}"
    const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashScreen}"
    const val exoPlayer = "com.google.android.exoplayer:exoplayer:${Versions.exoPlayer}"
    const val SONAR_CLOUD = "org.sonarqube"
    const val LOMBOK = "org.projectlombok:lombok:${Versions.lombok}"

    object Media {
        const val media3Common = "androidx.media3:media3-common:${Versions.media3}"
        const val media3Exoplayer = "androidx.media3:media3-exoplayer:${Versions.media3}"
        const val media3Session = "androidx.media3:media3-session:${Versions.media3}"
        const val core = "androidx.media:media:${Versions.media}"
    }

    object Appcompat {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
        const val activityKtx = "androidx.activity:activity-ktx:${Versions.activityKtx}"
    }

    object AndroidX {
        const val paging = "androidx.paging:paging-runtime-ktx:${Versions.paging}"
        const val pagingCommon = "androidx.paging:paging-common:${Versions.paging}"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.constraintLayoutCore}"
        const val lifecycleService =
            "androidx.lifecycle:lifecycle-service:${Versions.lifecycleService}"
        const val lifecycleRuntimeKtx =
            "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntimeKtx}"
    }

    object Compose {
        const val composeBOM = "androidx.compose:compose-bom:${Versions.composeBOM}"
        const val composeUI = "androidx.compose.ui:ui"
        const val composeUIGraphics = "androidx.compose.ui:ui-graphics"
        const val uiTooling = "androidx.compose.ui:ui-tooling"
        const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
        const val runtime = "androidx.compose.runtime:runtime"
        const val foundation = "androidx.compose.foundation:foundation"
        const val iconsCore = "androidx.compose.material:material-icons-core"
        const val iconsExtended = "androidx.compose.material:material-icons-extended"

        const val material3 = "androidx.compose.material3:material3"
        const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
        const val lifecycleViewmodelCompose =
            "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycleViewmodelCompose}"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout-compose:${Versions.constraintLayout}"
        const val lottieCompose = "com.airbnb.android:lottie-compose:${Versions.lottieCompose}"
        const val pagingCompose = "androidx.paging:paging-compose:${Versions.pagingCompose}"
        const val coilCompose = "io.coil-kt.coil3:coil-compose:${Versions.coil}"
        const val coilNetworkOkhttp = "io.coil-kt.coil3:coil-network-okhttp:${Versions.coil}"
    }

    object Hilt {
        const val hiltCommon = "androidx.hilt:hilt-common:${Versions.hiltCommon}"
        const val core = "com.google.dagger:hilt-android:${Versions.hilt}"
        const val compiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
        const val testing = "com.google.dagger:hilt-android-testing:${Versions.hilt}"
        const val navigationCompose =
            "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigationCompose}"
    }

    object Navigation {
        const val nav3Runtime = "androidx.navigation3:navigation3-runtime:${Versions.nav3Core}"
        const val nav3Ui = "androidx.navigation3:navigation3-ui:${Versions.nav3Core}"
        const val nav3ViewModel = "androidx.lifecycle:lifecycle-viewmodel-navigation3:${Versions.lifecycleViewmodelNav3}"
        const val nav3SerializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerializationCore}"
        const val nav3Adaptive = "androidx.compose.material3.adaptive:adaptive-navigation3:${Versions.material3AdaptiveNav3}"
        const val navigationCommonKtx = "androidx.navigation:navigation-common-ktx:${Versions.navigationCommonKtx}"
        const val nav3Plugin = "org.jetbrains.kotlin.plugin.serialization:${Versions.kotlinSerialization}"
    }

    object WorkManager {
        const val workRuntimeKtx = "androidx.work:work-runtime-ktx:${Versions.workManager}"
        const val workMultiProcess = "androidx.work:work-multiprocess:${Versions.workManager}"
        const val workTesting = "androidx.work:work-testing:${Versions.workManager}"
        const val workHilt = "androidx.hilt:hilt-work:${Versions.workHilt}"
        const val daggerHiltCompiler =
            "com.google.dagger:hilt-compiler:${Versions.daggerHiltCompiler}"
        const val hiltCompiler = "androidx.hilt:hilt-compiler:${Versions.hiltCompiler}"
    }

    object Networking {
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val retrofitMoshiConverter =
            "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
        const val retrofitGsonConverter =
            "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"

        const val chuckerDebug = "com.github.chuckerteam.chucker:library:${Versions.chucker}"
        const val chuckerRelease =
            "com.github.chuckerteam.chucker:library-no-op:${Versions.chucker}"
    }

    object Okhttp {
        const val bom = "com.squareup.okhttp3:okhttp-bom:${Versions.okhttp}"
        const val core = "com.squareup.okhttp3:okhttp"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor"
        const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp}"
    }

    object Moshi {
        const val core = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
        const val codeGen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
        const val lazyAdapter = "com.serjltt.moshi:moshi-lazy-adapters:${Versions.moshiLazyAdapter}"
        const val moshipack = "com.daveanthonythomas.moshipack:moshipack:${Versions.moshipack}"
    }

    object Jackson {
        const val core = "com.fasterxml.jackson.core:jackson-core:${Versions.jackson}"
        const val annotations = "com.fasterxml.jackson.core:jackson-annotations:${Versions.jackson}"
        const val databinding = "com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}"
        const val jacksonKotlin =
            "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}"
        const val jacksonMsgPackDataFormat =
            "org.msgpack:jackson-dataformat-msgpack:${Versions.jacksonMsgPackDataFormat}"
    }

    object Storage {
        const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
        const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
        const val dataStore = "androidx.datastore:datastore:${Versions.dataStore}"
        const val dataStorePref = "androidx.datastore:datastore-preferences:${Versions.dataStore}"
        const val securePref = "androidx.security:security-crypto-ktx:${Versions.securePref}"
    }

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}"
        const val coroutineCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutine}"
        const val coroutine =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutine}"
        const val coroutineTest =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutine}"
    }

    object Firebase {
        const val bom = "com.google.firebase:firebase-bom:${Versions.firebase}"
        const val analytics = "com.google.firebase:firebase-analytics"
        const val crashlytics = "com.google.firebase:firebase-crashlytics"
        const val remoteConfig = "com.google.firebase:firebase-config"
    }

    object Test {
        const val junit = "junit:junit:${Versions.junit}"
        const val junitExt = "androidx.test.ext:junit:${Versions.junitExt}"
        const val junitKtx = "androidx.test.ext:junit-ktx:${Versions.junitKxt}"
        const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
        const val uiTooling = "androidx.compose.ui:ui-tooling"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest"
        const val mockitoCore = "org.mockito:mockito-core:${Versions.mockito}"
        const val mockito = "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}"
        const val hamcrest = "org.hamcrest:hamcrest-library:${Versions.hamcrest}"
        const val hamcrestCore = "org.hamcrest:hamcrest-core:${Versions.hamcrest}"
        const val mockk = "io.mockk:mockk:${Versions.mockk}"
        const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
        const val truth = "com.google.truth:truth:${Versions.truth}"
        const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
        const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.mockWebServer}"
        const val json = "org.json:json:${Versions.json}"
    }

    object Google {
        const val libphonenumber = "com.googlecode.libphonenumber:libphonenumber:${Versions.libPhoneNumber}"
        const val gson = "com.google.code.gson:gson:${Versions.gson}"
    }

    object ExcelReader {
        const val excelReader = "org.apache.poi:poi-ooxml:${Versions.excelReader}"
    }

    object OpenTelemetry {
        const val desugaring = "com.android.tools:desugar_jdk_libs:${Versions.desugaring}"
        const val otelBom = "io.opentelemetry:opentelemetry-bom:${Versions.otelBom}"
        const val otelApi = "io.opentelemetry:opentelemetry-api"
        const val otelContext = "io.opentelemetry:opentelemetry-context"
        const val otelExporterOtlp = "io.opentelemetry:opentelemetry-exporter-otlp"
        const val otelExporterLogging = "io.opentelemetry:opentelemetry-exporter-logging"
        const val otelKtx = "io.opentelemetry:opentelemetry-extension-kotlin"
        const val otelSdk = "io.opentelemetry:opentelemetry-sdk"
        const val otelSemConv = "io.opentelemetry:opentelemetry-semconv:${Versions.otelSemConv}"
        const val otelTracingShim = "io.opentelemetry:opentelemetry-opentracing-shim:${Versions.otelTracingShim}"
        const val otelPropagators = "io.opentelemetry:opentelemetry-extension-trace-propagators:${Versions.otelPropagators}"
        const val otelGrpcProtobuf = "io.grpc:grpc-protobuf:${Versions.otelGrpcProtobuf}"
        const val otelGrpcStub = "io.grpc:grpc-stub:${Versions.otelGrpcStub}"
        const val otelGrpcNettyShaded = "io.grpc:grpc-netty-shaded:${Versions.otelGrpcNettyShaded}"
        const val otelOkhttp3 = "io.opentelemetry.instrumentation:opentelemetry-okhttp-3.0:${Versions.otelOkhttp3}"
        const val otelAndroid = "io.opentelemetry.android:android-agent:${Versions.otelAndroid}"
        const val otelAndroidCommon = "io.opentelemetry.android:common:${Versions.otelAndroid}"
        const val otelAndroidInstrumentationApi = "io.opentelemetry.android:instrumentation-common-api:${Versions.otelAndroid}"
        const val otelAndroidInstrumentationStartup = "io.opentelemetry.android:instrumentation-startup:${Versions.otelAndroid}"
        const val otelAndroidInstrumentationLifecycle = "io.opentelemetry.android:instrumentation-lifecycle:${Versions.otelAndroid}"
        const val otelAndroidInstrumentationActivity = "io.opentelemetry.android:instrumentation-activity:${Versions.otelAndroid}"
        const val otelAndroidInstrumentationAnr = "io.opentelemetry.android:instrumentation-anr:${Versions.otelAndroid}"
        const val otelAndroidInstrumentationCrash = "io.opentelemetry.android:instrumentation-crash:${Versions.otelAndroid}"
        const val otelAndroidInstrumentationFragment = "io.opentelemetry.android:instrumentation-fragment:${Versions.otelAndroid}"
        const val otelAndroidInstrumentationNetwork = "io.opentelemetry.android:instrumentation-network:${Versions.otelAndroid}"
        const val otelAndroidOkhttpAgent = "io.opentelemetry.android:okhttp-3.0-agent:${Versions.otelAndroid}"
        const val otelAndroidOkhttpLibrary = "io.opentelemetry.android:okhttp-3.0-library:${Versions.otelAndroid}"
        const val otelAndroidInstrumentationSlowrendering = "io.opentelemetry.android:instrumentation-slowrendering:${Versions.otelAndroid}"
    }
}

object Modules {
    const val dataModel = ":data:model"
    const val dataLocal = ":data:local"
    const val dataRemote = ":data:remote"
    const val dataRepository = ":data:repository"

    const val authenticator = ":domain:authenticator"
    const val commonComponents = ":libraries:components"
    const val librariesFramework = ":libraries:framework"
    const val librariesTestUtils = ":libraries:testutils"

    const val featureSplash = ":features:splash"
    const val featureDashboard = ":features:dashboard"
    const val featureHome = ":features:home"
    const val featureSettings = ":features:settings"
    const val featureAuthentication = ":features:authentication"
}
