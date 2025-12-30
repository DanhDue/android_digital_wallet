import org.gradle.api.JavaVersion

object AppConfig {
    const val namespace = "com.danhdue.androiddigitalwallet"
    const val applicationId = "com.danhdue.androiddigitalwallet"
    const val compileSdk = 36
    const val buildToolsVersion = "36.0.0"
    const val minSdk = 28
    const val targetSdk = 36

    private const val majorVersion = 3
    private const val minorVersion = 1
    private const val patchVersion = 1
    private const val prefixVersion = "VF8-ROW-"
    private const val postfixVersion = "-Beta"
    const val versionCode = majorVersion * 100000 + minorVersion * 100 + patchVersion
    const val versionName = "$prefixVersion$majorVersion.$minorVersion.$patchVersion$postfixVersion"

    const val androidTestInstrumentation = "androidx.test.runner.AndroidJUnitRunner"
    const val proguardOptimizedFileName = "proguard-android-optimize.txt"
    const val proguardConsumerRules = "proguard-rules.pro"
    const val jvmTarget = "21"
    const val kotlinCompilerExtensionVersion = "1.5.15"
    const val kotlinVersion = "2.3"

    val sourceCompatibility = JavaVersion.VERSION_21
    val targetCompatibility = JavaVersion.VERSION_21

    const val abiFilter = "arm64-v8a"
}
