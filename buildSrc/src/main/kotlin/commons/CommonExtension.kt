package commons

import AppConfig
import com.android.build.api.dsl.CommonExtension
import java.io.File

/**
 * load a property from local property files.
 */
fun getLocalProperty(
    key: String,
    file: String = "local.properties",
): Any {
    val properties = java.util.Properties()
    val localProperties = File(file)
    if (localProperties.isFile) {
        java.io.InputStreamReader(java.io.FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else {
        error("File from not found")
    }

    return properties.getProperty(key)
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
        this.sourceCompatibility = AppConfig.sourceCompatibility
        this.targetCompatibility = AppConfig.targetCompatibility
    }

    lint {
        // Turns off checks for the issue IDs you specify.
        disable += "TypographyFractions" + "TypographyQuotes" + "MissingTranslation"
        // Turns on checks for the issue IDs you specify. These checks are in
        // addition to the default lint checks.
        enable += "RtlHardcoded" + "RtlCompat" + "RtlEnabled"
        // To enable checks for only a subset of issue IDs and ignore all others,
        // list the issue IDs with the 'check' property instead. This property overrides
        // any issue IDs you enable or disable using the properties above.
        checkOnly += "NewApi" + "InlinedApi"
        // If set to true, turns off analysis progress reporting by lint.
        quiet = true
        checkReleaseBuilds = false
        // If set to true (default), stops the build if errors are found.
        abortOnError = false
        // If set to true, lint only reports errors.
        ignoreWarnings = true
        // If set to true, lint also checks all dependencies as part of its analysis.
        // Recommended for projects consisting of an app with library dependencies.
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
 * Adds the base default app configurations on Gradle.
 */
fun CommonExtension<*, *, *, *, *, *>.addDefaultConfig() {
    compileSdk {
        version = release(AppConfig.compileSdk)
    }
    defaultConfig {
        this.minSdk = AppConfig.minSdk
        this.testInstrumentationRunner = AppConfig.androidTestInstrumentation
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        this.sourceCompatibility = AppConfig.sourceCompatibility
        this.targetCompatibility = AppConfig.targetCompatibility
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

    lint {
        // Turns off checks for the issue IDs you specify.
        disable += "TypographyFractions" + "TypographyQuotes" + "MissingTranslation"
        // Turns on checks for the issue IDs you specify. These checks are in
        // addition to the default lint checks.
        enable += "RtlHardcoded" + "RtlCompat" + "RtlEnabled"
        // To enable checks for only a subset of issue IDs and ignore all others,
        // list the issue IDs with the 'check' property instead. This property overrides
        // any issue IDs you enable or disable using the properties above.
        checkOnly += "NewApi" + "InlinedApi"
        // If set to true, turns off analysis progress reporting by lint.
        quiet = true
        checkReleaseBuilds = false
        // If set to true (default), stops the build if errors are found.
        abortOnError = false
        // If set to true, lint only reports errors.
        ignoreWarnings = true
        // If set to true, lint also checks all dependencies as part of its analysis.
        // Recommended for projects consisting of an app with library dependencies.
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
 * Adds the base default app configurations on Gradle.
 */
fun CommonExtension<*, *, *, *, *, *>.addLibDefaultConfig() {
    compileSdk {
        version = release(AppConfig.compileSdk)
    }
    defaultConfig {
        this.minSdk = AppConfig.minSdk
        this.testInstrumentationRunner = AppConfig.androidTestInstrumentation
        proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
    }

    compileOptions {
        this.sourceCompatibility = AppConfig.sourceCompatibility
        this.targetCompatibility = AppConfig.targetCompatibility
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }

    lint {
        // Turns off checks for the issue IDs you specify.
        disable += "TypographyFractions" + "TypographyQuotes" + "MissingTranslation"
        // Turns on checks for the issue IDs you specify. These checks are in
        // addition to the default lint checks.
        enable += "RtlHardcoded" + "RtlCompat" + "RtlEnabled"
        // To enable checks for only a subset of issue IDs and ignore all others,
        // list the issue IDs with the 'check' property instead. This property overrides
        // any issue IDs you enable or disable using the properties above.
        checkOnly += "NewApi" + "InlinedApi"
        // If set to true, turns off analysis progress reporting by lint.
        quiet = true
        checkReleaseBuilds = false
        // If set to true (default), stops the build if errors are found.
        abortOnError = false
        // If set to true, lint only reports errors.
        ignoreWarnings = true
        // If set to true, lint also checks all dependencies as part of its analysis.
        // Recommended for projects consisting of an app with library dependencies.
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
