plugins {
    id("danhdue.android.feature")
    id("danhdue.android.compose")
}

android {
    namespace = "com.danhdue.home"
}

dependencies {
    implementation(project(":libraries:framework"))
    implementation(project(":libraries:components"))

    implementation(project(":features:myWallet"))
    implementation(project(":features:transactions"))
    implementation(project(":features:scanner"))
    implementation(project(":features:trends"))
    implementation(project(":features:settings"))
}
