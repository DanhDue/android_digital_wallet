import extensions.addStorageDependencies
import extensions.implementation

// `:core` — the dependency floor for every module (mirrors Flutter `packages/core`).
//
// Extracted from the god-module `libraries/framework` (epic `android_super_app_template`,
// design §4.1). Owns the framework-agnostic primitives: `DataState` / `NetworkResponse`
// call-adapter, `DispatcherProvider`, `extension/*`, `pref/*` (DataStore + Tink),
// `room/*` (`BaseDao`, converters), `session/SessionManager`, `usecase/*`, `utils/*`,
// the `Logger` contract and the `AppInitializer` init contract.
//
// Hard rule: NO Compose, no feature knowledge — this is what keeps Konsist K7
// ("`:core` is the floor") meaningful. It applies `commons.android-library` +
// `commons.dagger-hilt` only, never `commons.android-compose`, and declares no
// `project(...)` dependency.
plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_DAGGER_HILT)
}

android {
    namespace = "com.danhdue.core"
}

// `:core` is the Compose-free dependency floor (Konsist K7). Modern `androidx.activity`
// drags in `androidx.compose.runtime:runtime-annotation` (an annotations-only jar:
// @Stable / @Immutable — no Compose runtime, compiler or UI) transitively via
// `androidx.navigationevent`. Nothing in `:core` uses those annotations, so drop the
// edge to keep the module's classpath provably Compose-free.
configurations.configureEach {
    exclude(group = "androidx.compose.runtime", module = "runtime-annotation")
    exclude(group = "androidx.compose.runtime", module = "runtime-annotation-android")
}

dependencies {
    // Storage — Room (room/), DataStore + Tink + security-crypto (pref/).
    addStorageDependencies()
    // Paging — usecase/FlowPagingUseCase.
    implementation(Deps.AndroidX.paging)
    // JSON — extension/MoshiExtension.
    implementation(Deps.Moshi.core)
    // Retrofit + OkHttp — the NetworkResponse CallAdapter primitives (network/calladapter/).
    implementation(Deps.Networking.retrofit)
    implementation(platform(Deps.Okhttp.bom))
    implementation(Deps.Okhttp.core)
}
