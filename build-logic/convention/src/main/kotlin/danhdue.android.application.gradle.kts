import danhdue.convention.AppConfig

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("danhdue.android.compose")
    id("danhdue.android.hilt")
    // id("com.google.devtools.ksp")
}

android {
    defaultConfig {
        targetSdk = danhdue.convention.AppConfig.targetSdk
        versionCode = danhdue.convention.AppConfig.versionCode
        versionName = danhdue.convention.AppConfig.versionName
    }

    // Application specific config
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile(AppConfig.proguardOptimizedFileName),
                AppConfig.proguardConsumerRules,
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    flavorDimensions += "environment"

    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            buildConfigField("String", danhdue.convention.EnvConfigs.BuildConfigKey.DB_NAME, "\"${danhdue.convention.EnvConfigs.Development.dbName}\"")
            buildConfigField("Boolean", danhdue.convention.EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, "${danhdue.convention.EnvConfigs.Development.crashlyticsEnable}")
            buildConfigField("Boolean", danhdue.convention.EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, "${danhdue.convention.EnvConfigs.Development.analyticsEnable}")

            // Signing
            // signingConfig = signingConfigs.getByName(danhdue.convention.EnvConfigs.Development.signingConfigName)
            // Commenting out signingConfig assignment safe check needed or ensure 'debug' signing config exists always.
            // verifying if 'debug' signing config is available by default in android plugin? Yes.
            signingConfig = signingConfigs.getByName("debug")
        }

        create("stg") {
            dimension = "environment"
            applicationIdSuffix = ".stg"
            versionNameSuffix = "-stg"

            buildConfigField("String", danhdue.convention.EnvConfigs.BuildConfigKey.DB_NAME, "\"${danhdue.convention.EnvConfigs.Staging.dbName}\"")
            buildConfigField("Boolean", danhdue.convention.EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, "${danhdue.convention.EnvConfigs.Staging.crashlyticsEnable}")
            buildConfigField("Boolean", danhdue.convention.EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, "${danhdue.convention.EnvConfigs.Staging.analyticsEnable}")

            signingConfig = signingConfigs.getByName("debug")
        }

        create("prd") {
            dimension = "environment"

            buildConfigField("String", danhdue.convention.EnvConfigs.BuildConfigKey.DB_NAME, "\"${danhdue.convention.EnvConfigs.Production.dbName}\"")
            buildConfigField("Boolean", danhdue.convention.EnvConfigs.BuildConfigKey.CRASHLYTIC_IS_ENABLE, "${danhdue.convention.EnvConfigs.Production.crashlyticsEnable}")
            buildConfigField("Boolean", danhdue.convention.EnvConfigs.BuildConfigKey.ANALYTIC_IS_ENABLE, "${danhdue.convention.EnvConfigs.Production.analyticsEnable}")

            signingConfig = signingConfigs.getByName("debug")
        }
    }
}
