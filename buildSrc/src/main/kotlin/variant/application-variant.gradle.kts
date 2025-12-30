package variant
import extensions.buildStringConfigField

plugins {
    id("com.android.application")
}

android {

    flavorDimensions += listOf("version")
    productFlavors {
        create(EnvConfigs.ProductFlavors.Global) {
            dimension = "version"
            buildStringConfigField(EnvConfigs.BuildConfigKey.CAR_MODEL_NAME, EnvConfigs.CarModel.Global)

        }
        create(EnvConfigs.ProductFlavors.VFe34) {
            dimension = "version"
            applicationId = AppConfig.applicationVFe34Id
            buildStringConfigField(EnvConfigs.BuildConfigKey.CAR_MODEL_NAME, EnvConfigs.CarModel.CarVF34)
        }
        create(EnvConfigs.ProductFlavors.VF8) {
            dimension = "version"
            buildStringConfigField(EnvConfigs.BuildConfigKey.CAR_MODEL_NAME, EnvConfigs.CarModel.CarVF35)
        }
    }
}
