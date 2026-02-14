plugins {
    `kotlin-dsl`
}

group = "com.danhdue.androiddigitalwallet.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.android)
    compileOnly(libs.ksp)
}
