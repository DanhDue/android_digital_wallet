import danhdue.convention.AppConfig
import danhdue.convention.EnvConfigs
import danhdue.convention.addLibDefaultConfig
import danhdue.convention.buildBooleanConfigField
import danhdue.convention.buildStringConfigField

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    // id("codeanalyzetools.quality")
    // id("codeanalyzetools.jacoco-report")
    // id("codeanalyzetools.spotless")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    addLibDefaultConfig()

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            
            buildStringConfigField(EnvConfigs.BuildConfigKey.DB_NAME, EnvConfigs.Production.dbName)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, EnvConfigs.Production.crashlyticsEnable)
            buildBooleanConfigField(EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, EnvConfigs.Production.analyticsEnable)
        
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
            
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
    // coreLibraryDesugaring(libs.desugar.jdk.libs) // If defined
    
    // Common
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    
    // Test
    testImplementation("junit:junit:4.13.2")
}
