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
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = AppConfig.kotlinCompilerExtensionVersion
    }

    compileOptions {
        this.sourceCompatibility = AppConfig.sourceCompatibility
        this.targetCompatibility = AppConfig.targetCompatibility
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
    this.compileSdk = AppConfig.compileSdk
    defaultConfig {
        this.minSdk = AppConfig.minSdk
        this.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        this.sourceCompatibility = AppConfig.sourceCompatibility
        this.targetCompatibility = AppConfig.targetCompatibility
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
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
