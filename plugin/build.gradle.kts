import extensions.implementation
import extensions.ksp
import extensions.testImplementation

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
}

android {
    namespace = "com.danhdue.plugin"
}

dependencies {
    implementation(Deps.Dagger.core)
    ksp(Deps.Dagger.compiler)
    implementation(Deps.WorkManager.workRuntimeKtx)
    testImplementation(Deps.WorkManager.workTesting)
}
