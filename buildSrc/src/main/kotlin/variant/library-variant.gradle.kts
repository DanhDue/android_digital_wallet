package variant

import AppConfig
import Deps
import EnvConfigs

plugins {
    id("com.android.library")
}

android {
    flavorDimensions += listOf("version")
    productFlavors {
        create(EnvConfigs.ProductFlavors.Global) {
            dimension = "version"
            sourceSets {
                getByName(EnvConfigs.ProductFlavors.Global) {
                    java.srcDirs("src/global")
                }
            }
        }
        create(EnvConfigs.ProductFlavors.VFe34) {
            dimension = "version"

            sourceSets {
                getByName(EnvConfigs.ProductFlavors.VFe34) {
                    aidl.srcDirs("src/vfe34/aidl", "src\\vfe34\\aidl", "src\\vfe34\\aidl")
                    java.srcDirs("src/vfe34/java", "src\\vfe34\\java", "src\\vfe34\\java")
                }
            }
        }
        create(EnvConfigs.ProductFlavors.VF8) {
            dimension = "version"

            sourceSets {
                getByName(EnvConfigs.ProductFlavors.VF8) {
                    java.srcDirs("src/vf8/java", "src\\vf8\\java")
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = AppConfig.sourceCompatibility
        targetCompatibility = AppConfig.targetCompatibility
    }
}

dependencies {
    coreLibraryDesugaring(Deps.OpenTelemetry.desugaring)
}
