import extensions.addFirebaseDependencies
import extensions.addFlipperDependencies
import extensions.addNavigationDependencies
import extensions.api
import extensions.implementation

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
    id(Deps.COMMONS_DAGGER_HILT)
}

android {
    namespace = "com.danhdue.framework"
}

dependencies {
    // Cross-feature seam: re-exported so framework consumers keep resolving
    // com.danhdue.platform.EntryProviderInstaller / LocalEntryProviderInstallers.
    // Left as `api` here — Task 9 narrows the surface; consumers still resolve transitively.
    api(project(":platform"))
    // :core primitives (coroutines / extension / session / usecase / utils / DataState /
    // AppInitializer). Used directly by the app-lifecycle plumbing and MVVM base.
    // Re-exported (`api`) so existing framework consumers — :app and every :features:* —
    // keep resolving them transitively until the full rewire in Task 9.
    api(project(":core"))
    // :network — FlipperBackstackObserver references com.danhdue.network.flipper.
    // FlipperNavigationObject. Re-exported (`api`) so existing framework consumers keep
    // resolving the network types transitively until the full rewire in Task 9.
    api(project(":network"))
    // MultiDexInitializer / CoreApplication (MultiDexApplication).
    implementation(Deps.multidex)
    // navigation3 runtime — Navigator / NestedNavigator / CommonRoutes (NavKey).
    addNavigationDependencies()
    // FirebaseCrashlyticsReportTree.
    addFirebaseDependencies()
    // FlipperBackstackObserver — debug/release Flipper runtime wiring.
    addFlipperDependencies()
}
