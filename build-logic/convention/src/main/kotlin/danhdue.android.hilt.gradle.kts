plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

dependencies {
    "implementation"("com.google.dagger:hilt-android:2.55")
    "ksp"("com.google.dagger:hilt-android-compiler:2.55")
    "androidTestImplementation"("com.google.dagger:hilt-android-testing:2.55")
    "kspAndroidTest"("com.google.dagger:hilt-android-compiler:2.55")
    "testImplementation"("com.google.dagger:hilt-android-testing:2.55")
}
