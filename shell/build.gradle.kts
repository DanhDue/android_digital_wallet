import extensions.CORE
import extensions.FRAMEWORK
import extensions.PLATFORM
import extensions.UI_KIT
import extensions.addNavigationDependencies

// `:shell` — Host-only tab shell (mirrors Flutter `lib/shell/**`).
//
// Relocated from `features/home` in epic `android_super_app_template` (design §4.1, §9 Phase 2,
// Task 10): the tab-shell UI (`ShellScreen` / `ShellRoot` / `ShellViewModel` + bottom nav + the
// per-tab nested `NavDisplay`) always was the Shell, misplaced in a feature module. It builds
// the `NavDisplay` purely from the Hilt `Set<EntryProviderInstaller>` (`LocalEntryProviderInstallers`)
// and provides one `NestedNavigator` per tab (`LocalNestedNavigator`) — it does not reimplement
// the navigation3 mechanism, it consumes it.
//
// `:shell` is a Host, NOT a feature: it applies `commons.android-library` (+ `-compose`,
// `dagger-hilt`) rather than `commons.android-feature`, so the cross-feature Gradle guard does
// not fire on its deps. Task 11 relocated every tab's seed `*Route` NavKey into
// `:platform.AppRoutes` and moved cross-feature signalling onto `AppEventBus`, so `:shell` no
// longer needs a single `:features:*` dependency — `:app` still aggregates every feature's
// Hilt `@IntoSet EntryProviderInstaller` at the `@HiltAndroidApp` root.
plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
    id(Deps.COMMONS_DAGGER_HILT)
}

android {
    namespace = "com.danhdue.shell"
}

dependencies {
    // Package modules — explicit (no `commons.android-feature` auto-wiring for a Host module).
    CORE
    PLATFORM
    FRAMEWORK
    UI_KIT

    addNavigationDependencies()
}
