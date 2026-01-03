plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("AndroidCoreLibraryPlugin") {
            id = "android.core.library.plugin"
            implementationClass = "commons.AndroidCoreLibraryPlugin"
        }
    }
}

object GlobalVersions {
    const val GRADLE = "8.13.2"
    const val KOTLIN = "2.1.0"
    const val KSP = "2.1.0-1.0.29"
    const val HILT = "2.57.2"
    const val GOOGLE_SERVICE = "4.4.4"
    const val CRASHLYTICS = "3.0.6"
    const val VERSION_CHECKER = "0.53.0"
    const val KT_LINT = "14.0.1"
    const val SPOTLESS = "8.1.0"
    const val DETEKT = "1.23.8"
}

object GlobalDeps {
    const val ANDROID_GRADLE = "com.android.tools.build:gradle:${GlobalVersions.GRADLE}"
    const val KOTLIN_GRADLE = "org.jetbrains.kotlin:kotlin-gradle-plugin:${GlobalVersions.KOTLIN}"
    const val KSP_PROCESSING = "com.google.devtools.ksp:symbol-processing:${GlobalVersions.KSP}"
    const val KSP_PROCESSING_CMDLINE =
        "com.google.devtools.ksp:symbol-processing-cmdline:${GlobalVersions.KSP}"
    const val KSP_PROCESSING_GRADLE_PLUGIN =
        "com.google.devtools.ksp:symbol-processing-gradle-plugin:${GlobalVersions.KSP}"
    const val KSP_GRADLE_PLUGIN =
        "com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${GlobalVersions.KSP}"
    const val KSP_API_GRADLE_PLUGIN =
        "com.google.devtools.ksp:symbol-processing-api:${GlobalVersions.KSP}"
    const val KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:${GlobalVersions.KOTLIN}"
    const val HILT = "com.google.dagger:hilt-android-gradle-plugin:${GlobalVersions.HILT}"
    const val CRASHLYTICS =
        "com.google.firebase:firebase-crashlytics-gradle:${GlobalVersions.CRASHLYTICS}"
    const val VERSION_CHECKER =
        "com.github.ben-manes:gradle-versions-plugin:${GlobalVersions.VERSION_CHECKER}"
    const val KTLINT = "org.jlleitschuh.gradle:ktlint-gradle:${GlobalVersions.KT_LINT}"
    const val SPOTLESS = "com.diffplug.spotless:spotless-plugin-gradle:${GlobalVersions.SPOTLESS}"
    const val DETEKT = "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${GlobalVersions.DETEKT}"
    const val COMPOSE_GRADLE = "org.jetbrains.kotlin:compose-compiler-gradle-plugin:${GlobalVersions.KOTLIN}"
}

dependencies {
    implementation(GlobalDeps.ANDROID_GRADLE)
    implementation(GlobalDeps.KOTLIN_GRADLE)
    implementation(GlobalDeps.KOTLIN_STDLIB)
    implementation(GlobalDeps.KSP_PROCESSING)
    implementation(GlobalDeps.KSP_PROCESSING_CMDLINE)
    implementation(GlobalDeps.KSP_PROCESSING_GRADLE_PLUGIN)
    implementation(GlobalDeps.KSP_GRADLE_PLUGIN)
    implementation(GlobalDeps.KSP_API_GRADLE_PLUGIN)
    implementation(GlobalDeps.HILT)
    implementation(GlobalDeps.CRASHLYTICS)
    implementation(GlobalDeps.VERSION_CHECKER)
    implementation(GlobalDeps.KTLINT)
    implementation(GlobalDeps.SPOTLESS)
    implementation(GlobalDeps.DETEKT)
    implementation(GlobalDeps.COMPOSE_GRADLE)
}
