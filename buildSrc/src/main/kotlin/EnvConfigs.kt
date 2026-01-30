object EnvConfigs {

    val FreeCompilerArgs = listOf(
        "-Xjvm-default=all",
        "-opt-in=kotlin.RequiresOptIn",

        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.FlowPreview",
        "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
        "-opt-in=com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi",
        "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
    )

    val FreeCoroutineCompilerArgs = listOf(
        "-Xjvm-default=all",
        "-opt-in=kotlin.RequiresOptIn",

        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.FlowPreview"
    )

    object BuildConfigKey {
        const val crashlyticsEnableKey = "crashlyticsEnabled"
        const val analyticsEnableKey = "analyticsEnabled"

        const val debugSigningConfigName = "debug"
        const val releaseAndroid9SigningName = "releaseAndroid9"
        const val releaseAndroid9SigningConfigFile = "signing/android9.signing.properties"
        const val releaseAndroid11SigningName = "releaseAndroid11"
        const val releaseAndroid11SigningConfigFile = "signing/android11.signing.properties"

        const val DB_NAME = "DB_NAME"
        const val CRASHLYTIC_IS_ENABLE = "CRASHLYTIC_IS_ENABLE"
        const val ANALYTIC_IS_ENABLE = "ANALYTIC_IS_ENABLE"
    }

    object Release {
        const val dbName = "ZenoDb"

        const val crashlyticsEnable = true
        const val analyticsEnable = true
        const val openTelemetryEndPoint = "http://10.0.2.2:4317"
        const val BASE_URL = "https://digital-wallet-93c4ba68a41d.herokuapp.com/api/v1/"
    }

    object Debug {
        const val dbName = "ZenoDb"

        const val crashlyticsEnable = false
        const val analyticsEnable = false
        const val openTelemetryEndPoint = "http://10.0.2.2:4317"
        const val BASE_URL = "https://digital-wallet-93c4ba68a41d.herokuapp.com/api/v1/"
    }
}
