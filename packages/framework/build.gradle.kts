import extensions.addFirebaseDependencies
import extensions.addNavigationDependencies
import extensions.api
import extensions.implementation

// `:framework` — the MVI base + the navigation3 host mechanism (mirrors Flutter `packages/framework`).
//
// Third split of the former `libraries/framework` god-module (epic `android_super_app_template`,
// design §4.1), relocated to the repo root. Owns `MviViewModel` / `MvvmViewModel` /
// `BaseViewState`, and the unchanged navigation3 mechanism: `Navigator`
// (`@ActivityRetainedScoped` backstack), `NestedNavigator` + `LocalNestedNavigator` (per-tab
// nested backstack), plus the app-lifecycle plumbing
// (`CoreApplication`, `TimberInitializer`, `MultiDexInitializer`).
//
// Depends solely on `:core` (design's `core <- {framework, network, ui_kit, platform}` fan-out).
// It does NOT depend on `:platform`: no `:framework` source imports `com.danhdue.platform.*` —
// features reach the cross-feature seam through the `commons.android-feature` convention, not
// transitively through here.
plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
    id(Deps.COMMONS_DAGGER_HILT)
}

android {
    namespace = "com.danhdue.framework"
}

dependencies {
    // `api` (kept): `MvvmViewModel.execute(callFlow: Flow<DataState<T>>)` is a `protected`
    // member of the public `abstract class MvvmViewModel`, inherited by `MviViewModel` and by
    // every feature ViewModel. A subclass in a feature module that calls `execute(...)` needs
    // `com.danhdue.core.network.DataState` on its own compile classpath, so `:core` must be a
    // transitive (`api`) dependency of `:framework`, not an implementation detail. The
    // `AppInitializer` supertype of `TimberInitializer` / `MultiDexInitializer` is also from
    // `:core`. (Konsist K7 still guarantees `:core` imports nothing from up here.)
    api(project(":packages:core"))

    // MultiDexInitializer / CoreApplication (MultiDexApplication).
    implementation(Deps.multidex)
    // navigation3 runtime — Navigator / NestedNavigator / CommonRoutes (NavKey).
    addNavigationDependencies()
    // FirebaseCrashlyticsReportTree.
    addFirebaseDependencies()
}
