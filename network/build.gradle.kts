import extensions.addFlipperDependencies
import extensions.api
import extensions.debugImplementation
import extensions.implementation
import extensions.releaseImplementation

// `:network` — the HTTP stack for every module (mirrors Flutter `packages/network`).
//
// Second split of the god-module `libraries/framework` (epic `android_super_app_template`,
// design §4.1). Owns the OkHttp / Retrofit / Moshi wiring: `NetworkCoreModule` (the Hilt
// graph — base `OkHttpClient`, `Retrofit.Builder`, `@Named("BaseUrl")`), the interceptors
// (`GlobalHeaderInterceptor`, `HttpRequestInterceptor`, `EnvironmentInterceptor`), the
// `apiCall` / `Failure` error mapping, the Flipper network/navigation tooling and
// `FlipperInitializer`, plus `NetworkConfig`.
//
// The framework-agnostic primitives it builds on — `DataState`, `HttpStatusCode` and the
// `NetworkResponse` call-adapter (`network/calladapter/`) — already live in `:core`.
//
// Hard rule: NO Compose (Konsist K7 keeps `:core` the floor and `:network` a headless HTTP
// tier). It applies `commons.android-library` + `commons.dagger-hilt` only, never
// `commons.android-compose`, and its only `project(...)` edge is `:core`.
plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_DAGGER_HILT)
}

android {
    namespace = "com.danhdue.network"
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        getByName("release") {
            buildConfigField("String", "BASE_URL", "\"${EnvConfigs.Release.BASE_URL}\"")
        }
        getByName("debug") {
            buildConfigField("String", "BASE_URL", "\"${EnvConfigs.Debug.BASE_URL}\"")
        }
    }
}

// Keep `:network` provably Compose-free (verification: `:network:dependencies
// --configuration debugCompileClasspath | grep -i compose` must be empty). Modern
// `androidx.activity` (pulled in transitively via `addCommonDependencies()`) drags the
// annotations-only `androidx.compose.runtime:runtime-annotation` jar; nothing here uses
// `@Stable` / `@Immutable`, so drop the edge — same treatment as `:core`.
configurations.configureEach {
    exclude(group = "androidx.compose.runtime", module = "runtime-annotation")
    exclude(group = "androidx.compose.runtime", module = "runtime-annotation-android")
}

dependencies {
    // Re-exported (`api`) so `:network` consumers keep resolving `DataState` /
    // `NetworkResponse` / `HttpStatusCode` transitively — matches the `:core` / `:platform`
    // re-export precedent in `libraries/framework` until the full rewire in Task 9.
    api(project(":core"))

    // Retrofit / Moshi converter — `Retrofit.Builder` and `MoshiConverterFactory` appear in
    // the public (and `inline`) surface of `NetworkCoreModule` / `NetworkHelper`.
    api(Deps.Networking.retrofit)
    api(Deps.Networking.retrofitMoshiConverter)

    // OkHttp — `OkHttpClient` / `Interceptor` / `HttpLoggingInterceptor` are public types
    // consumed by `AuthNetworkModule` and every feature network module.
    api(platform(Deps.Okhttp.bom))
    api(Deps.Okhttp.core)
    api(Deps.Okhttp.loggingInterceptor)

    // Moshi (reflection adapter) — `Moshi` / `EnumValueJsonAdapter` are public API.
    api(Deps.Moshi.core)

    // Chucker — `ChuckerInterceptor` is public in `NetworkHelper`; no-op flavour on release.
    debugImplementation(Deps.Networking.chuckerDebug)
    releaseImplementation(Deps.Networking.chuckerRelease)

    // Flipper — `FlipperInitializer` / `Flipper*Object` load these purely by reflection, so
    // they are runtime-only (debug) and never a compile dependency; kept for parity with the
    // environment the code had inside `libraries/framework`.
    addFlipperDependencies()
}
