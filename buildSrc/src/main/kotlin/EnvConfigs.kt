object EnvConfigs {

    val FreeCompilerArgs = listOf(
        "-Xjvm-default=all",
        "-opt-in=kotlin.RequiresOptIn",
        "-opt-in=kotlin.Experimental",
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
        "-opt-in=kotlin.Experimental",
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.FlowPreview"
    )

    object BuildConfigKey {
        const val crashlyticsEnableKey = "crashlyticsEnabled"
        const val analyticsEnableKey = "analyticsEnabled"

        const val debugSigningConfigName = "debug"
        const val debugSigningKeystoreFile = "signing/vinbdi_vf_assistant_keystore.jks"
        const val releaseAndroid9SigningName = "releaseAndroid9"
        const val releaseAndroid9SigningConfigFile = "signing/android9.signing.properties"
        const val releaseAndroid11SigningName = "releaseAndroid11"
        const val releaseAndroid11SigningConfigFile = "signing/android11.signing.properties"

        const val DB_NAME = "DB_NAME"
        const val CRASHLYTIC_IS_ENABLE = "CRASHLYTIC_IS_ENABLE"
        const val ANALYTIC_IS_ENABLE = "ANALYTIC_IS_ENABLE"
        const val CAR_MODEL_NAME = "CAR_MODEL_NAME"
    }

    object Release {
        const val vaSdkBaseUrl = "https://one.vinbase.ai/api/"
        const val apiBaseUrl = "https://dev-cloud.vinbase.ai"
        const val wssBaseUrl = "wss://dev-cloud.vinbase.ai"
        const val authenBaseUrl = "https://dev-iam.vinbase.ai"
        const val dbName = "VAGloabalDb"

        const val crashlyticsEnable = true
        const val analyticsEnable = true
        const val openTelemetryEndPoint = "http://10.0.2.2:4317"
    }

    object Debug {
        const val vaSdkBaseUrl = "https://one.vinbase.ai/api/"
        const val apiBaseUrl = "https://dev-cloud.vinbase.ai"
        const val wssBaseUrl = "wss://dev-cloud.vinbase.ai"
        const val authenBaseUrl = "https://dev-iam.vinbase.ai"
        const val dbName = "VAGloabalDb"

        const val crashlyticsEnable = false
        const val analyticsEnable = false
        const val openTelemetryEndPoint = "http://10.0.2.2:4317"
    }

    object CarModel {
        const val Global = "Global"

        const val CarVF34 = "VFe34"
        const val CarVF35 = "VFe35v" // VN market

        const val CarVF36 = "VF9v" // VN market

        const val CarVF5 = "VF5"
        const val CarVF6 = "VF6"
        const val CarVF7 = "VF7"
    }

    object ProductFlavors {
        const val Global = "global"
        const val VFe34 = "vfe34"
        const val VF8 = "vf8"
        const val VF9 = "vf9"
    }
}
