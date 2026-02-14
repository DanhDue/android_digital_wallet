import extensions.addFirebaseDependencies

plugins {
    alias(libs.plugins.danhdue.android.library)
    alias(libs.plugins.danhdue.android.compose)
}

android {
    namespace = "com.danhdue.libraries.components"
}

dependencies {
    addFirebaseDependencies()
    implementation(Deps.splashScreen)
    implementation(project(":libraries:jetframework"))
}
