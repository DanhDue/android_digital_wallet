---
id: "task_2_deeplink_contract"
status: "todo"
priority: "high"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-09T20:50:45Z"
completedAt: null
labels: ["architecture", "feature", "platform"]
order: "a2"
---

# Task 2: Two-Tier Contract Types and AppDeepLinks

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

Declare the contract both tiers speak, plus the two small extensions to existing `:platform` types that the engine needs.

**Tier 1** — the only shared table, one line per feature:

```kotlin
data class FeatureEntryPoint(
    val feature: String,
    val entryRoute: NavKey,
    val tab: Int?,
    val dynamicModule: String? = null,
    val requiresAuth: Boolean = false,
)

object AppDeepLinks { val entryPoints: List<FeatureEntryPoint> = listOf(/* settings, scanner */) }
```

**Tier 2** — what each feature declares for itself: `DeepLinkResolver`, `DeepLinkTarget`, `Placement`.

**Command vocabulary** — `NavigationCommand`, `FailureReason`.

**Two edits to existing types**:

- `FeatureEntry` gains `fun resolver(): DeepLinkResolver? = null`. It **must** have a default so `ScannerFeatureEntry` keeps compiling untouched — this task changes no feature.
- `AppEvent` gains `data object UserLoggedIn`, symmetric with the existing `UserLoggedOut`.

`tab` is `Int`, not `ShellTab`: `:platform` must not depend on `:shell` (the dependency runs the other way). The index is already the shell's own vocabulary — `ShellTab.Home(0)` / `Scanner(1)` / `Settings(2)`.

## Relevant Files & Context Pointers

- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/DeepLinkResolver.kt` — **new** (resolver, target, placement)
- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/AppDeepLinks.kt` — **new** (tier 1)
- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/NavigationCommand.kt` — **new**
- `packages/platform/src/main/kotlin/com/danhdue/platform/FeatureEntry.kt` — **modify**, add defaulted method
- `packages/platform/src/main/kotlin/com/danhdue/platform/AppEvent.kt` — **modify**, add `UserLoggedIn`
- `packages/platform/src/main/kotlin/com/danhdue/platform/AppRoutes.kt` — read; `entryRoute` values come from here
- `packages/platform/src/test/kotlin/com/danhdue/platform/deeplink/AppDeepLinksTest.kt` — **new**
- `features/scanner/src/main/java/com/danhdue/scanner/presentation/di/ScannerFeatureEntry.kt` — read only; must still compile unchanged
- `shell/src/main/kotlin/com/danhdue/shell/ShellState.kt` — read only, for the `ShellTab` index values

## Design Rationale

- **Extending `FeatureEntry` with a defaulted method, not a second discovery channel.** DFM resolvers must reach the host at runtime; `FeatureEntry` + `ServiceLoader` already does exactly that for navigation entries. Adding a parallel `ServiceLoader` interface would double the aggregated `META-INF/services` bookkeeping in `:app` for no gain.
- **`placement` is nullable and defaults are derived from tier 1** — `null` → `InTab(entryPoint.tab, parents = listOf(entryPoint.entryRoute))`, or `RootFullScreen` when `tab == null`. Features therefore do not restate what tier 1 already knows. The derivation itself is implemented in Task 4; this task only fixes the contract and documents it.
- **`AppEvent.UserLoggedIn` on the existing bus, not a new channel.** A login signal is genuinely fire-and-forget: if nobody is listening there is no pending link to replay, so `replay = 0` loses nothing. Contrast the navigation commands, which need guaranteed delivery and get their own `Channel` in Task 4.
- **`Placement` is a `sealed interface`** so every `when` over it is exhaustive — the same posture as `AppEvent`.

Applicable skill: `.agents/skills/test-driven-development` (for the `AppDeepLinks` invariant tests). Run `.agents/skills/quality_check` at the end.

## TDD Checklist

**TDD Adaptation, stated explicitly**: most of this task is type and constant declaration, which has no behaviour to drive out with a failing test. RED/GREEN/REFACTOR is applied only to the two `AppDeepLinks` invariants, which are real assertions. The declarations themselves are verified by compilation plus the DoD checks below.

- [ ] **RED**: Write `AppDeepLinksTest`, failing first:
  - every `feature` key in `entryPoints` is unique
  - every `entryRoute` is declared under `com.danhdue.platform` (a cross-boundary route, per Konsist K9) and not inside a feature package
- [ ] **GREEN**: Declare all contract types and populate `AppDeepLinks.entryPoints` with `settings` (tab 2) and `scanner` (tab 1, `dynamicModule = "scanner"`).
- [ ] **Non-TDD, verified by compilation**: add `FeatureEntry.resolver()` with its default and `AppEvent.UserLoggedIn`; confirm `features/scanner` compiles with **zero** edits.
- [ ] **REFACTOR**: KDoc every public type. `FeatureEntry.resolver()` must state why it is defaulted; `Placement` must state the derivation rule for `null`.

## Definition of Done

- [ ] `./gradlew :packages:platform:testDebugUnitTest` green.
- [ ] `./gradlew :features:scanner:compileDebugKotlin` green **without any edit to that module** — proves the `FeatureEntry` change is source-compatible.
- [ ] `./gradlew :konsist-test:test` green — K9 still passes.
- [ ] `./gradlew detekt spotlessCheck assembleDebug` green.
- [ ] No `:shell` import anywhere in `:platform` (`tab` stays an `Int`).

## Dependencies & Blockers

- Blocked by [Task 1](task_1_deeplink_parser.md) — `DeepLinkResolver.resolve` takes a `DeepLink`.
- Blocks [Task 3](task_3_deeplink_guard_chain.md), [Task 4](task_4_deeplink_router_pipeline.md), [Task 11](task_11_settings_resolver.md), [Task 12](task_12_scanner_dfm_resolver.md).

## References & Rollback

- Source spec §5.2, §5.3 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- Konsist K9 — `konsist-test/src/test/kotlin/com/danhdue/konsist/HostRulesTest.kt`
- **Rollback**: revert the two edits to `FeatureEntry` / `AppEvent` and delete the new files. Because `resolver()` is defaulted and `UserLoggedIn` is additive to a sealed hierarchy nobody exhaustively matches yet, neither edit has callers to unwind.
