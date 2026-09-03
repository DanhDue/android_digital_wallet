import extensions.addFirebaseDependencies
import extensions.implementation

// `:ui_kit` — the shared Compose design system + UI helpers (mirrors Flutter `packages/ui_kit`).
//
// Formed in epic `android_super_app_template` (design §4.1, Task 8) by merging the two
// Compose-only support modules that sat at the same layer:
//   - the former "components" library — theme (`ui/theme/`), reusable widgets
//     (`ui/widgets/`), and the text-emphasis examples (`examples/`).
//   - the former "jetframework" library — the top-level Compose helpers
//     (`ClickableSingle`, `FirebaseAnalytic`, `LanguageHelper`, `LinkText`,
//     `RememberFlow`, `TimedVisibility`, `WindowInfo`) and the runtime-permission
//     handlers (`permission/`).
// Both trees are now repackaged under the single namespace `com.danhdue.uikit`; the
// former cross-module edge between them is now an in-module reference.
//
// Depends only on `:core` (design's `core <- {framework, network, ui_kit, platform}`
// fan-out). It applies `commons.android-compose` (Compose) on top of
// `commons.android-library` (which also brings quality / spotless / jacoco). No Hilt:
// nothing in the merged code uses `@Inject` / Dagger. No `:framework`: no merged file
// imports `com.danhdue.framework.*`.
plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_ANDROID_COMPOSE)
}

android {
    namespace = "com.danhdue.uikit"
}

dependencies {
    // `:core` — the dependency floor (design §4.1). No other module edge.
    implementation(project(":infra:core"))
    // FirebaseAnalytic (`rememberAnalytics`) — com.google.firebase.analytics.FirebaseAnalytics.
    addFirebaseDependencies()
    // core-splashscreen — consumed by the splash widget.
    implementation(Deps.splashScreen)
}
