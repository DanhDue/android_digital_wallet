---
id: "task_6_shell_execute_placement"
status: "done"
priority: "high"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-10T04:54:00Z"
completedAt: "2026-09-10T04:54:00Z"
labels: ["architecture", "feature", "shell"]
order: "a6"
---

# Task 6: ShellViewModel Executes Placement Commands

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

`ShellViewModel` becomes the single consumer of `DeepLinkRouter.commands` and the only place that executes navigation. It is the only component holding both halves of what execution needs: the three nested back stacks (its own state) and the root `Navigator`.

Handle the three placement commands:

| Command | Effect |
|---|---|
| `OpenInTab(tab, stack)` | select that tab and **replace** its nested back stack with `stack` |
| `OpenFullScreen(destination)` | `navigator.navigateTo(destination)` on the root back stack; tabs untouched |
| `OpenInCurrentTab(destination)` | append to the currently selected tab's back stack |

**Replace, not append**, for `OpenInTab`: "synthesise" means *the stack must look like this*, so opening the same link twice yields the same state. Appending would grow the stack on every open.

**No-op when the destination is already on top** — for all three commands. Prevents flicker and screen-state loss when a user re-taps the link that is already open.

### Step zero: verify an unproven assumption

The design assumes `ShellViewModel` can inject the root `Navigator`: `@HiltViewModel` lives in `ViewModelComponent`, a child of `ActivityRetainedComponent`, where `Navigator` is `@ActivityRetainedScoped`. **This has not been compiled and must be proven before the rest of the task proceeds.**

If it fails, the documented fallback is: `MainActivity` collects the `OpenFullScreen` branch and `:shell` collects the rest. That still works and costs only the single-executor property. Take the fallback and record it in the epic HLD — do not spend time fighting the DI graph.

## Relevant Files & Context Pointers

- `shell/src/main/kotlin/com/danhdue/shell/ShellViewModel.kt` — **modify**, inject `DeepLinkRouter` + `Navigator`, collect commands
- `shell/src/main/kotlin/com/danhdue/shell/ShellState.kt` — read; back stacks and `ShellTab` indices
- `packages/framework/src/main/java/com/danhdue/framework/navigation/Navigator.kt` — read; `navigateTo` / `popBackStack` / `backStack`
- `app/src/main/kotlin/com/danhdue/androiddigitalwallet/di/AppNavigationModule.kt` — read; where `Navigator` is `@ActivityRetainedScoped`-provided
- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/NavigationCommand.kt` — read
- `shell/src/test/kotlin/com/danhdue/shell/ShellViewModelTest.kt` — **modify/extend**
- `shell/src/test/kotlin/com/danhdue/shell/DeepLinkEntryPointContractTest.kt` — **new**, the tab-index contract test

## Design Rationale

- **`:shell` is the executor because `:shell` owns the back stacks.** Putting execution in `:platform` would require `:platform` to know about tabs, inverting the module dependency. Putting it in `:app` would require `:app` to know the tab layout, which is `:shell`'s concern.
- **The tab-index contract test lives here, not in `:platform`.** `AppDeepLinks.entryPoints` uses `Int` tab indices because `:platform` must not depend on `:shell`. That leaves nothing in `:platform` able to check the index is valid. `:shell` is the only module that sees both, so the assertion "every `FeatureEntryPoint.tab` is a valid `ShellTab` index" belongs here — and it runs in CI's existing `testDebugUnitTest` step, so a bad index fails the build.
- **Collect in `init` via `safeLaunch`, not in a composable.** `ShellViewModel` survives configuration changes, so a single retained collector consumes each buffered command exactly once. Collecting in composition would re-subscribe on every recomposition and risk double execution.

Applicable skills: `.agents/skills/test-driven-development`; `.agents/skills/systematic-debugging` for step zero if the `Navigator` injection fails; `.agents/skills/quality_check` at the end.

## TDD Checklist

- [x] **Step zero (spike, before TDD)**: add the `Navigator` constructor parameter and compile `:app`. Confirm the Hilt graph resolves. If it does not, switch to the documented fallback and note it in the HLD before continuing.
- [x] **RED**: extend `ShellViewModelTest` with a fake `DeepLinkRouter` exposing a controllable command flow. Failing first:
  - `OpenInTab(2, [SettingsRoute, ProfileRoute])` selects the Settings tab and **replaces** `settingsBackStack`
  - `OpenInTab` on a tab that already has a deeper stack replaces it rather than appending
  - `OpenInTab` whose destination is already on top is a no-op — state instance unchanged
  - `OpenFullScreen` calls `navigator.navigateTo` and leaves all three tab stacks untouched
  - `OpenFullScreen` is a no-op when the destination is already on top of the root stack
  - `OpenInCurrentTab` appends to the selected tab only
  - an out-of-range tab index is ignored, not a crash
  - two commands in sequence are both executed, in order
- [x] **RED**: `DeepLinkEntryPointContractTest` — every `AppDeepLinks.entryPoints` entry with a non-null `tab` maps to a real `ShellTab` index.
- [x] **GREEN**: implement the collector and the three handlers.
- [x] **REFACTOR**: extract a `topOf(tab)` helper so the no-op check is written once, not three times.

## Definition of Done

- [x] Step zero resolved and its outcome recorded (injection works, or the fallback is adopted and the HLD updated).
- [x] `./gradlew :shell:testDebugUnitTest` green, all cases above.
- [x] Calling `deepLinkRouter.dispatch("myapp://settings/profile")` from anywhere in the app navigates correctly — **internal navigation (source 4) is functional at the end of this task**, verified manually.
- [x] `./gradlew :konsist-test:test detekt spotlessCheck assembleDebug` green.
- [x] No `:shell` → `:features:*` import was added (Konsist K1/K6 unchanged).

## Dependencies & Blockers

- Blocked by [Task 4](task_4_deeplink_router_pipeline.md) — needs `DeepLinkRouter` and `NavigationCommand`.
- Blocked by [Task 5](task_5_shell_per_module_state.md) — builds on the refactored state.
- Blocks [Task 7](task_7_shell_ensure_module_and_failure.md).

## References & Rollback

- Source spec §6.5, §7.1 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- Epic HLD §5 — the `Navigator` injection risk and its fallback
- **Rollback**: remove the collector from `ShellViewModel.init` and the two injected dependencies. The engine returns to being unreachable but intact; no external surface exists yet.
