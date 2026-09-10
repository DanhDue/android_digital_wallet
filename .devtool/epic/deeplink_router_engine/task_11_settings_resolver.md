---
id: "task_11_settings_resolver"
status: "done"
priority: "medium"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-10T09:16:20Z"
completedAt: "2026-09-10T09:16:20Z"
labels: ["feature", "example"]
order: "a11"
---

# Task 11: SettingsDeepLinkResolver — Install-Time Exemplar

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

The first tier-2 resolver, and the pattern every install-time feature copies.

`SettingsDeepLinkResolver` maps:

| URI | Target |
|---|---|
| `myapp://settings` | `DeepLinkTarget(AppRoutes.SettingsRoute)` |
| `myapp://settings/profile` | `DeepLinkTarget(ProfileRoute)` |
| anything else under `settings` | `null` — not ours |

Contributed via Hilt `@Provides @IntoSet DeepLinkResolver` in the feature's existing `presentation/di` package — the exact mirror of how `SettingsNavigationModule` already contributes its `EntryProviderInstaller`.

**Neither target declares `placement`.** Both rely on the derived default from tier 1 (`InTab(tab = 2, parents = [SettingsRoute])`), demonstrating that the common case needs no ceremony: `myapp://settings/profile` selects the Settings tab and synthesises the stack `[SettingsRoute, ProfileRoute]`, so Back returns to Settings.

`ProfileRoute` is **feature-private** and stays that way — it lives in `com.danhdue.settings.presentation.profile`, is referenced only by this feature's own resolver and nav module, and must **not** be promoted to `AppRoutes` (Konsist K9 requires promotion only for keys used *outside* their feature; the resolver is inside it).

**Criterion 4.1 proof point**: the resolver's tests must pass under `:features:settings:testDebugUnitTest` alone, with no `:app` and no host. That is the whole reason `DeepLinkParser` is pure JVM.

## Relevant Files & Context Pointers

- `features/settings/src/main/kotlin/com/danhdue/settings/presentation/di/SettingsDeepLinkResolver.kt` — **new**
- `features/settings/src/main/kotlin/com/danhdue/settings/presentation/di/SettingsNavigationModule.kt` — **modify**, add the `@IntoSet` binding; read for the established style
- `features/settings/src/main/kotlin/com/danhdue/settings/presentation/profile/ProfileRoute.kt` — read; the private target
- `packages/platform/src/main/kotlin/com/danhdue/platform/AppRoutes.kt` — read; `SettingsRoute`
- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/AppDeepLinks.kt` — read; the `settings` entry point already declared in Task 2
- `features/settings/src/test/kotlin/com/danhdue/settings/presentation/di/SettingsDeepLinkResolverTest.kt` — **new**
- `features/settings/build.gradle.kts` — verify `:packages:platform` is already a dependency (it is)

## Design Rationale

- **The resolver sits beside the nav module, in `presentation/di`.** Same package, same lifecycle, same reviewer. This placement is also what Konsist K10 (Task 13) will enforce, so the exemplar must obey the rule it establishes.
- **`when` on `link.segments`, not regex.** Segments are already parsed and normalised by Task 1; a regex over the raw string would re-derive what the parser produced and drift from it.
- **Returning `null` for unknown sub-paths is required, not defensive.** The router asks resolvers in turn and takes the first non-null; a resolver that claimed everything under its prefix would block a sibling resolver in the same feature and mask a real `NoResolver` failure.
- **No `placement` on purpose.** The exemplar must demonstrate the *default* path, because that is what most links use. Task 12 demonstrates an explicit `placement`.
- **Unknown params are ignored, not rejected.** Links from older or newer app versions may carry extra query keys; ignoring them keeps links forward-compatible.

Applicable skills: `.agents/skills/test-driven-development`; `.agents/skills/quality_check` at the end.

## TDD Checklist

- [x] **RED**: `SettingsDeepLinkResolverTest`, failing first:
  - `myapp://settings` → target `SettingsRoute`, `placement == null`
  - `myapp://settings/profile` → target `ProfileRoute`, `placement == null`
  - `myapp://settings/unknown` → `null`
  - `myapp://settings/profile/extra` → `null` (depth is exact, not prefix)
  - a link whose `feature` is not `settings` → `null`
  - unknown query params do not change the outcome
  - `requiresAuth` is `false` on both targets — Settings is reachable signed out
- [x] **GREEN**: implement the resolver and the `@IntoSet` binding.
- [x] **REFACTOR**: KDoc pointing at this class as the install-time reference implementation, and naming Task 12 as the on-demand counterpart.

## Definition of Done

- [x] **`./gradlew :features:settings:testDebugUnitTest` green when run on its own** — the criterion 4.1 proof point; run it as an isolated command, not as part of a full build.
- [x] `ProfileRoute` remains feature-private; `AppRoutes` gains nothing.
- [x] `./gradlew :konsist-test:test` green — K1, K9 unaffected.
- [x] End-to-end manual: `adb shell am start -a android.intent.action.VIEW -d "myapp://settings/profile"` opens Profile in the Settings tab and Back returns to Settings.
- [x] `./gradlew detekt spotlessCheck assembleDebug` green.

## Dependencies & Blockers

- Blocked by [Task 2](task_2_deeplink_contract.md) — needs the contract types.
- Blocked by [Task 10](task_10_mainactivity_intent_handling.md) for the end-to-end manual check; the unit tests need only Task 2.
- Blocks [Task 14](task_14_mason_brick_deeplink.md) — the brick generates a copy of this shape.

## References & Rollback

- Source spec §5.3 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- `features/settings/.../di/SettingsNavigationModule.kt` — the `@IntoSet` pattern being mirrored
- **Rollback**: delete the resolver and its binding. `myapp://settings/*` then resolves to `Failed(NoResolver)`, which is handled gracefully; nothing else regresses.
