plugins {
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

dependencies {
    "implementation"("com.google.dagger:hilt-android:2.57.2")
    "ksp"("com.google.dagger:hilt-android-compiler:2.57.2")
    "androidTestImplementation"("com.google.dagger:hilt-android-testing:2.57.2")
    "kspAndroidTest"("com.google.dagger:hilt-android-compiler:2.57.2")
    "testImplementation"("com.google.dagger:hilt-android-testing:2.57.2")
}
