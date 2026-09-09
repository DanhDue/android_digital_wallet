---
id: "task_5_shell_per_module_state"
status: "todo"
priority: "high"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-09T20:50:45Z"
completedAt: null
labels: ["refactor", "shell"]
order: "a5"
---

# Task 5: Per-Module Install State in ShellState

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

`ShellState` currently hard-codes install tracking for exactly one dynamic feature:

```kotlin
val scannerInstalling: Boolean = false,
val scannerReady: Boolean = false,
```

and `ShellViewModel` carries `private const val SCANNER_MODULE = "scanner"`.

`NavigationCommand.EnsureModule(module, replay)` must work for **any** on-demand module, so these become:

```kotlin
val installingModules: Set<String> = emptySet(),
val readyModules: Set<String> = emptySet(),
```

`ShellScreen` reads `state.readyModules` instead of `state.scannerReady` for two decisions: whether to load `ServiceLoader` installers, and whether the Scanner tab shows a spinner.

**This task changes no behaviour.** The Scanner tab must install, show progress and mount exactly as it does today. It is a pure widening of a data shape, split out from Tasks 6/7 precisely so that one PR is not simultaneously a refactor and a feature.

This also closes the documentation drift recorded in the epic HLD §2.2: `features/scanner/build.gradle.kts` describes a `shell/.../navigation/OnDemandFeatures.kt` registry that was never written. No such file is created — `AppDeepLinks.entryPoints` already carries `dynamicModule`, and this task makes the shell state general enough to use it.

## Relevant Files & Context Pointers

- `shell/src/main/kotlin/com/danhdue/shell/ShellState.kt` — **modify**, the two booleans and their KDoc
- `shell/src/main/kotlin/com/danhdue/shell/ShellViewModel.kt` — **modify**, `ensureScannerInstalled()` becomes module-parameterised; remove the `SCANNER_MODULE` constant
- `shell/src/main/kotlin/com/danhdue/shell/ShellScreen.kt` — **modify**, `remember(state.scannerReady)` and the spinner condition
- `shell/src/test/**` — **modify** existing `ShellViewModel` tests to the new shape
- `features/scanner/build.gradle.kts` — **modify** the stale comment block that names `OnDemandFeatures.kt`
- `packages/platform/src/main/kotlin/com/danhdue/platform/FeatureInstaller.kt` — read; `ensureInstalled(module, onReady)` is unchanged

## Design Rationale

- **`Set<String>` over `Map<String, InstallState>`.** Two disjoint sets express the three reachable states (absent / installing / ready) without an enum, and `copy(readyModules = readyModules + module)` stays a one-liner in `reduce`. A richer state machine would be speculative — nothing in the epic needs "failed" as a persisted per-module state, because a failure surfaces as a one-shot `ShellEvent` in Task 7.
- **Keep tab selection driving the install.** Selecting the Scanner tab still triggers the install, exactly as today; Task 7 adds `EnsureModule` as a *second* trigger. Both converge on the same private function, so there is one install path, not two.
- **Immutability preserved.** `ShellState` is an MVI state — the sets are replaced via `copy`, never mutated.

Applicable skill: `.agents/skills/quality_check` at the end, per `CRITICAL_RULES.md`.

## TDD Checklist

**TDD Adaptation, stated explicitly**: this is a pure refactor with no new behaviour, so RED/GREEN/REFACTOR does not literally apply — there is no new failing test to write, and inventing one would only assert the shape of a data class. The substitution is a concrete change list plus a regression gate on the existing suite:

- [ ] **Change**: replace the two booleans in `ShellState` with `installingModules` / `readyModules`; update the KDoc to describe module-keyed state instead of scanner-specific state.
- [ ] **Change**: parameterise `ShellViewModel.ensureScannerInstalled()` to `ensureModuleInstalled(module: String)`; delete `SCANNER_MODULE`; the Scanner tab passes `"scanner"` at the call site.
- [ ] **Change**: `ShellScreen` keys its `remember` on `state.readyModules` and gates the spinner on `"scanner" in state.installingModules`.
- [ ] **Change**: update existing `ShellViewModel` tests to the new state shape — assert set membership rather than booleans.
- [ ] **Change**: correct the stale `OnDemandFeatures.kt` reference in `features/scanner/build.gradle.kts`.
- [ ] **Regression gate**: `./gradlew :shell:testDebugUnitTest :konsist-test:test detekt spotlessCheck assembleDebug` all green.
- [ ] **Manual regression**: install the app, tap the Scanner tab, confirm the spinner appears and the scanner screen mounts — behaviour identical to before the change.

## Definition of Done

- [ ] No occurrence of `scannerReady`, `scannerInstalling` or `SCANNER_MODULE` remains in the repository (`grep` to confirm).
- [ ] `./gradlew :shell:testDebugUnitTest` green with updated assertions.
- [ ] `./gradlew assembleDebug bundleDebug` green.
- [ ] Manual Scanner-tab regression passes — spinner then content, as before.
- [ ] `features/scanner/build.gradle.kts` no longer references a file that does not exist.
- [ ] `./gradlew detekt spotlessCheck` clean.

## Dependencies & Blockers

- Blocked by: nothing in this epic — this refactor is independent of Phase 1 and may run in parallel with Tasks 1–4.
- Blocks [Task 6](task_6_shell_execute_placement.md), [Task 7](task_7_shell_ensure_module_and_failure.md).

## References & Rollback

- Source spec §7.3 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- Epic HLD §2.2 — the `OnDemandFeatures.kt` documentation drift
- **Rollback**: a single `git revert`. The change is confined to `:shell` plus one comment in `features/scanner/build.gradle.kts`, and no other module reads these fields.
