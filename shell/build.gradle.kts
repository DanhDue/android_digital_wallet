import extensions.CORE
import extensions.FEATURE_MY_WALLET
import extensions.FEATURE_SCANNER
import extensions.FEATURE_SETTINGS
import extensions.FEATURE_TRANSACTIONS
import extensions.FEATURE_TRENDS
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
// not fire on its `:features:*` deps. It MAY depend on multiple `:features:*` — that privilege
// is granted to `:app` / `:shell` only (Konsist K6-exempt). The 5 feature deps below are needed
// only until Task 11 relocates each tab's seed `*Route` NavKey into `:platform.AppRoutes`.
plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
    id(Deps.COMMONS_DAGGER_HILT)
}

android {
    namespace = "com.danhdue.shell"
}

dependencies {
    // Infrastructure — explicit (no `commons.android-feature` auto-wiring for a Host module).
    CORE
    PLATFORM
    FRAMEWORK
    UI_KIT

    // Cross-feature deps — Host privilege (Konsist K6). Task 11 drops these once each tab's
    // seed `*Route` moves to `:platform.AppRoutes`.
    FEATURE_MY_WALLET
    FEATURE_TRANSACTIONS
    FEATURE_SCANNER
    FEATURE_TRENDS
    FEATURE_SETTINGS

    addNavigationDependencies()
}
