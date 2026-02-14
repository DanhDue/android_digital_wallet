import extensions.addNetworkDependencies

plugins {
    alias(libs.plugins.danhdue.android.feature)
    alias(libs.plugins.danhdue.android.compose)
}

android {
    namespace = "com.danhdue.authentication"
}

dependencies {
    implementation(project(":libraries:framework"))
    implementation(project(":libraries:components"))

    // Network deps (migrating addNetworkDependencies)
    // Checking addNetworkDependencies content:
    // implementation(Libs.Networking.retrofit) ...
    // implementation(Libs.Networking.retrofitMoshiConverter)
    // addOkhttpDependencies() -> implementation(platform(Deps.Okhttp.bom))...
    // implementation(Deps.Moshi.core)...
    
    // For now, I'll use the extension if it's still available via import, 
    // BUT we are migrating away from buildSrc.
    // However, addNetworkDependencies is in buildSrc extensions.
    // I should copy the deps here or verify if framework already has them.
    // Framework has addNetworkDependencies().
    // Feature usually depends on framework for network.
    // Let's remove duplicate addNetworkDependencies() calls if covered by framework or add them explicitly.
    // The previous file had:
    // addNetworkDependencies()
    // addNetworkDependencies()
    // duplicate.
    
    // I will stick to just framework/component for now and see if build passes.
    // Authentication likely needs network for API calls, but framework might export it or it should be added.
    // extensions.addNetworkDependencies adds implementation, not api.
    // So authenticaton needs its own if it uses Retrofit directly.
    
    // Let's rely on what framework provides or add explicit deps if needed.
    // For now, valid strategy:
    // implementation(libs.retrofit)
    // implementation(libs.retrofit.converter.moshi)
    // implementation(libs.okhttp)
    // implementation(libs.okhttp.logging.interceptor)
    // implementation(libs.moshi.kotlin)
    
    // But to be safe and quick, I will just replicate the logic with libs aliases.
    
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.moshi.kotlin)

    // addNavigationDependencies() was commented out in previous steps for framework, but feature might need them?
    // Feature plugin already adds navigation dependencies! (danhdue.android.feature)
    // So no need to add them manually here.
}
