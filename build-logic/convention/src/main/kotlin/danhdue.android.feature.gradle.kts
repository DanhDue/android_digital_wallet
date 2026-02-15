import danhdue.convention.AppConfig
import danhdue.convention.EnvConfigs
import danhdue.convention.buildBooleanConfigField
import danhdue.convention.buildStringConfigField
import danhdue.convention.addLibDefaultConfig

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    // id("commons.dagger-hilt")
    // alias(libs.plugins.hilt)
    // id("codeanalyzetools.quality")
    // id("codeanalyzetools.jacoco-report")
    // id("codeanalyzetools.spotless")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {

    addLibDefaultConfig()

    buildTypes {
        release {
            buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Production.dbName)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, EnvConfigs.Production.crashlyticsEnable)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, EnvConfigs.Production.analyticsEnable)
        }

        debug {
            buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Development.dbName)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, EnvConfigs.Development.crashlyticsEnable)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, EnvConfigs.Development.analyticsEnable)
        }
    }

    /*
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(AppConfig.kotlinVersion))
        jvmTarget.set(AppConfig.jvmTarget)
        freeCompilerArgs.addAll(EnvConfigs.FreeCoroutineCompilerArgs)
    }
    */
}

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(AppConfig.kotlinVersion))
        jvmTarget.set(AppConfig.jvmTarget)
        freeCompilerArgs.addAll(EnvConfigs.FreeCoroutineCompilerArgs)
    }
}


dependencies {
    "coreLibraryDesugaring"("com.android.tools:desugar_jdk_libs:2.0.4")
    
    // Common
    "api"("com.jakewharton.timber:timber:5.0.1")
    "api"("androidx.core:core-ktx:1.15.0")
    "api"("androidx.appcompat:appcompat:1.7.0")
    "api"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    "api"("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    "api"("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    "api"("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    "api"("androidx.activity:activity-ktx:1.10.0")
    "compileOnly"("org.projectlombok:lombok:1.18.30")
    "annotationProcessor"("org.projectlombok:lombok:1.18.30")

    // Hilt
    "implementation"("com.google.dagger:hilt-android:2.57.2")
    "ksp"("com.google.dagger:hilt-android-compiler:2.57.2")
    "androidTestImplementation"("com.google.dagger:hilt-android-testing:2.57.2")
    "kspAndroidTest"("com.google.dagger:hilt-android-compiler:2.57.2")
    "testImplementation"("com.google.dagger:hilt-android-testing:2.57.2")
    
    // Compose Navigation
    // "implementation"("androidx.navigation3:navigation3-ui:2.9.0-alpha04")
    "implementation"("androidx.navigation3:navigation3-runtime:1.0.0")
    "implementation"("androidx.navigation3:navigation3-ui:1.0.0")
    "implementation"("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0")
    // "implementation"("androidx.compose.material3.adaptive:adaptive-navigation3:1.3.0-alpha05")
    "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.0")
    "implementation"("androidx.navigation:navigation-common-ktx:2.9.6")

    // Test
    "api"("junit:junit:4.13.2")
    "api"("androidx.test.ext:junit:1.2.1")
    "api"("androidx.test.espresso:espresso-core:3.6.1")
    "api"("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    "api"("io.mockk:mockk:1.13.10")
}
