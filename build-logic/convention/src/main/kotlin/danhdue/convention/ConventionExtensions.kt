package danhdue.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.dsl.BuildType

fun ProductFlavor.buildStringConfigField(name: String, value: String) {
    this.buildConfigField("String", name, "\"$value\"")
}

fun ProductFlavor.buildBooleanConfigField(name: String, value: Boolean) {
    this.buildConfigField("boolean", name, "$value")
}

fun DefaultConfig.buildStringConfigField(name: String, value: String) {
    this.buildConfigField("String", name, "\"$value\"")
}

fun DefaultConfig.buildBooleanConfigField(name: String, value: Boolean) {
    this.buildConfigField("boolean", name, "$value")
}

fun BuildType.buildStringConfigField(name: String, value: String) {
    this.buildConfigField("String", name, "\"$value\"")
}

fun BuildType.buildBooleanConfigField(name: String, value: Boolean) {
    this.buildConfigField("boolean", name, "$value")
}

/**
 * Adds the base default library configurations on Gradle.
 */
fun CommonExtension<*, *, *, *, *, *>.addLibDefaultConfig() {
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        testInstrumentationRunner = AppConfig.androidTestInstrumentation
        proguardFiles(AppConfig.proguardOptimizedFileName, AppConfig.proguardConsumerRules)
    }

    compileOptions {
        sourceCompatibility = AppConfig.sourceCompatibility
        targetCompatibility = AppConfig.targetCompatibility
    }

    buildFeatures {
        buildConfig = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }

    lint {
        disable += "TypographyFractions" + "TypographyQuotes" + "MissingTranslation"
        enable += "RtlHardcoded" + "RtlCompat" + "RtlEnabled"
        checkOnly += "NewApi" + "InlinedApi"
        quiet = true
        checkReleaseBuilds = false
        abortOnError = false
        ignoreWarnings = true
        checkDependencies = true
    }

    packaging {
        resources.excludes.apply {
            add("META-INF/AL2.0")
            add("META-INF/LGPL2.1")
            add("/META-INF/{AL2.0,LGPL2.1}")
            add("META-INF/DEPENDENCIES")
            add("META-INF/LICENSE")
            add("META-INF/LICENSE.txt")
            add("META-INF/license.txt")
            add("META-INF/LICENSE.md")
            add("META-INF/LICENSE-notice.md")
            add("META-INF/NOTICE")
            add("META-INF/NOTICE.txt")
            add("META-INF/notice.txt")
            add("META-INF/ASL2.0")
            add("META-INF/*.kotlin_module")
            add("META-INF/gradle/incremental.annotation.processors")
            add("/META-INF/{AL2.0,LGPL2.1,gradle-plugins}")
            jniLibs.pickFirsts.add("**/*.so")
        }
    }
}

/**
 * Adds the base Compose configurations on Gradle.
 */
fun CommonExtension<*, *, *, *, *, *>.addComposeConfig() {
    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = AppConfig.kotlinCompilerExtensionVersion
    }

    compileOptions {
        sourceCompatibility = AppConfig.sourceCompatibility
        targetCompatibility = AppConfig.targetCompatibility
    }

    // Configs from addLibDefaultConfig regarding lint and packaging are commonly needed but might be duplicated if both are called.
    // android-compose calls addLibDefaultConfig AND addComposeConfig usually.
}
