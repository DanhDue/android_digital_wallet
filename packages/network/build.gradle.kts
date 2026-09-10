import extensions.PLATFORM
import extensions.api
import extensions.debugImplementation
import extensions.implementation
import extensions.releaseImplementation

// `:network` — the HTTP stack for every module (mirrors Flutter `packages/network`).
//
// Second split of the former `framework` god-module (epic `android_super_app_template`,
// design §4.1). Owns the OkHttp / Retrofit / Moshi wiring: `NetworkCoreModule` (the Hilt
// graph — base `OkHttpClient`, `Retrofit.Builder`, `@Named("BaseUrl")`), the interceptors
// (`GlobalHeaderInterceptor`, `HttpRequestInterceptor`, `EnvironmentInterceptor`), the
// `apiCall` / `Failure` error mapping, plus `NetworkConfig`.
//
// The framework-agnostic primitives it builds on — `DataState`, `HttpStatusCode` and the
// `NetworkResponse` call-adapter (`network/calladapter/`) — already live in `:core`.
//
// Hard rule: NO Compose (Konsist K7 keeps `:core` the floor and `:network` a headless HTTP
// tier). It applies `commons.android-library` + `commons.dagger-hilt` only, never
// `commons.android-compose`. Its `project(...)` edges are `:core` and — since Task 11 —
// `:platform` (leaf; no cycle), so `UnauthorizedInterceptor` can publish
// `AppEvent.UserLoggedOut` on the `AppEventBus` when a 401 is not recovered.
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
    // `api` (kept — verified in Task 9): `:network`'s public surface exposes `:core` types.
    // `apiCall(call): DataState<T>` (public top-level fun in `ApiCallExtension.kt`) returns
    // `com.danhdue.core.network.DataState`, and `Throwable.handleThrowable()` /
    // `httpCodeToFailure(...)` consume `com.danhdue.core.network.HttpStatusCode`. Every feature
    // data layer calls `apiCall { ... }`, so `:core` must stay a transitive dependency here.
    api(project(":packages:core"))

    // `:platform` (leaf module — external deps only) — `UnauthorizedInterceptor` injects
    // `AppEventBus` and publishes `AppEvent.UserLoggedOut` on an unrecovered 401. `:platform`
    // does not depend on `:network`, so this edge is acyclic.
    PLATFORM

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
}
