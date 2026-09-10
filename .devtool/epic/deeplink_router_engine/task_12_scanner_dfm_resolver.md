---
id: "task_12_scanner_dfm_resolver"
status: "done"
priority: "high"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-10T09:17:50Z"
completedAt: "2026-09-10T09:17:50Z"
labels: ["feature", "example", "dfm"]
order: "a12"
---

# Task 12: Scanner DFM Resolver and Local-Testing Acceptance

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

The on-demand counterpart to Task 11, and the task that proves goal **D3** — that deeplinking did not break criterion 1.2.

`ScannerFeatureEntry` overrides the defaulted `resolver()` added in Task 2:

```kotlin
class ScannerFeatureEntry : FeatureEntry {
    override fun installer(): EntryProviderInstaller = { ... }   // unchanged
    override fun resolver(): DeepLinkResolver = ScannerDeepLinkResolver()
}
```

Mapping `myapp://scanner` → `DeepLinkTarget(AppRoutes.ScannerRoute)`.

**The critical difference from Task 11 is the delivery mechanism, not the mapping.** A dynamic feature module cannot use Hilt `@IntoSet` — its `@Module`s are invisible to the host graph at compile time. The resolver reaches the router at runtime through `ServiceLoader`, using the `META-INF/services/com.danhdue.platform.FeatureEntry` file **owned by `:app`**. That file already lists `ScannerFeatureEntry`, so **no new service registration is needed** — overriding `resolver()` is sufficient. Confirm this rather than assuming it.

Also demonstrate an **explicit `placement`** on at least one target (Task 11 demonstrates the derived default), so the template shows both halves of goal D6.

### The acceptance run that matters

Deeplink to `myapp://scanner` **with the split not installed**, and confirm the full chain: `EnsureModule` → `SplitInstallManager` → `SplitCompat.install` → `readyModules` update → replay → `ServiceLoader` finds the resolver → navigate. Verified with `bundletool --local-testing`, the mechanism the predecessor epic already established in CI.

## Relevant Files & Context Pointers

- `features/scanner/src/main/java/com/danhdue/scanner/presentation/di/ScannerFeatureEntry.kt` — **modify**, override `resolver()`
- `features/scanner/src/main/java/com/danhdue/scanner/presentation/di/ScannerDeepLinkResolver.kt` — **new**
- `app/src/main/resources/META-INF/services/com.danhdue.platform.FeatureEntry` — **read and confirm**; expected to need no change
- `features/scanner/build.gradle.kts` — read; explains why this module ships no Hilt and depends on `:app`
- `app/src/main/kotlin/com/danhdue/androiddigitalwallet/split/FeatureInstallerImpl.kt` — read; real install terminal states
- `shell/src/main/kotlin/com/danhdue/shell/ShellScreen.kt` — read; the `ServiceLoader` skip-don't-fail helpers
- `.github/workflows/ci.yml` — read; the existing bundletool `--local-testing` step to extend
- `.devtool/epic/android_super_app_template/task_14_scanner_dynamic_feature.md` — read; how the DFM was originally set up
- `features/scanner/src/test/java/com/danhdue/scanner/presentation/di/ScannerDeepLinkResolverTest.kt` — **new**

## Design Rationale

- **Override the defaulted `resolver()` rather than add a second `ServiceLoader` interface.** One discovery channel, one aggregated services file, one place `:app` has to maintain. A parallel channel would double the bookkeeping that `features/scanner/build.gradle.kts` warns about (bundletool rejects two feature splits shipping the same root resource with different content).
- **Plain instantiation, no Hilt.** This module ships no Hilt code by design — `com.android.dynamic-feature` cannot apply the Hilt plugin. `ScannerDeepLinkResolver` must have no injected dependencies; if a future resolver needs one, it comes through the `FeatureEntry` construction path, not a container.
- **`ScannerRoute` is already in `AppRoutes`**, promoted by Konsist K9 because it crosses a boundary. This is the concrete case where tier 1 and K9 coincide (HLD §4.1) — the host knows the route *before* the split exists, which is exactly what makes the pre-install lookup possible.
- **`--local-testing` over a real Play track.** It exercises `SplitInstallManager` end to end without a Play deployment, and CI already has the pinned, SHA-verified bundletool.

Applicable skills: `.agents/skills/test-driven-development` for the resolver; `.agents/skills/systematic-debugging` if `ServiceLoader` does not surface the resolver after install; `.agents/skills/quality_check` at the end.

## TDD Checklist

- [x] **RED**: `ScannerDeepLinkResolverTest`, failing first:
  - `myapp://scanner` → target `ScannerRoute`
  - `myapp://scanner/unknown` → `null`
  - a non-`scanner` feature key → `null`
  - the target carrying an explicit `placement` returns exactly that value, not the derived default
- [x] **GREEN**: implement the resolver and override `resolver()`.
- [x] **Verify (no new service registration)**: confirm `app/src/main/resources/META-INF/services/...FeatureEntry` still lists only `ScannerFeatureEntry` and needs no edit. If it does need one, that is a design surprise — record it in the HLD.
- [x] **Non-TDD acceptance, on device or emulator** (the install path cannot be unit-tested — `NoOpFeatureInstaller` reports every module ready):
  - build the AAB, generate APKs with `bundletool build-apks --local-testing`, install with `install-apks`
  - with the scanner split **absent**, fire `adb shell am start -a android.intent.action.VIEW -d "myapp://scanner"`
  - observe: spinner → split installs → Scanner screen opens
  - fire the same link again with the split now present: opens immediately, no spinner, no second install
  - force an install failure (airplane mode) and confirm the "couldn't load that feature" message, with **no** infinite retry loop — this exercises the replay-once cap from Task 4
- [x] **REFACTOR**: KDoc naming this the on-demand reference implementation and Task 11 as the install-time counterpart.

## Definition of Done

- [x] `./gradlew :features:scanner:testDebugUnitTest` green.
- [x] The `META-INF/services` file is unchanged (or the surprise is documented).
- [x] The three-step acceptance run passes: cold install via deeplink, warm re-open, and failure without a retry loop.
- [x] CI's bundletool step still asserts the scanner split is present in the generated `.apks`.
- [x] **Goal D3 demonstrated**: a deeplink to an uninstalled on-demand feature installs it and navigates.
- [x] `./gradlew :konsist-test:test detekt spotlessCheck assembleDebug bundleDebug` green.

## Dependencies & Blockers

- Blocked by [Task 2](task_2_deeplink_contract.md) — the defaulted `resolver()`.
- Blocked by [Task 7](task_7_shell_ensure_module_and_failure.md) — the `EnsureModule` branch.
- Blocked by [Task 10](task_10_mainactivity_intent_handling.md) — the external entry point for the acceptance run.

## References & Rollback

- Source spec §5.3, §6.3 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- Predecessor epic design §4.4 — the DFM contract this builds on
- [bundletool local testing](https://developer.android.com/guide/playcore/feature-delivery/on-demand#local-testing)
- **Rollback**: drop the `resolver()` override and delete the resolver. `myapp://scanner` degrades to `Failed(NoResolver)`; the Scanner **tab** keeps working exactly as before, since tab-driven install is a separate path.
