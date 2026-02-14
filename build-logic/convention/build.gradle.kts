plugins {
    `kotlin-dsl`
}

group = "com.danhdue.androiddigitalwallet.buildlogic"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.compose.compiler.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
}

gradlePlugin {
    plugins {
        // register("androidLibrary") {
        //     id = "danhdue.android.library"
        //     implementationClass = "Android_library_gradle"
        // }
        // register("androidFeature") {
        //     id = "danhdue.android.feature"
        //     implementationClass = "Android_feature_gradle"
        // }
        // register("androidCompose") {
        //     id = "danhdue.android.compose"
        //     implementationClass = "Android_compose_gradle"
        // }
        // register("androidHilt") {
        //     id = "danhdue.android.hilt"
        //     implementationClass = "Dagger_hilt_gradle"
        // }
    }
}
