---
id: "task_7_shell_ensure_module_and_failure"
status: "todo"
priority: "high"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-09T20:50:45Z"
completedAt: null
labels: ["architecture", "feature", "shell"]
order: "a7"
---

# Task 7: ShellViewModel Handles EnsureModule and Failed

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

The two remaining commands, both of which involve something other than a straight back-stack write.

**`EnsureModule(module, replay)`** — a link targets an on-demand feature whose split is not installed:

1. `installingModules += module` (the existing spinner appears)
2. `featureInstaller.ensureInstalled(module) { ... }`
3. on success: `readyModules += module`, `installingModules -= module`, then `deepLinkRouter.dispatch(replay)`
4. on failure: `installingModules -= module` and surface `Failed(InstallFailed)`

Reuse the private install function generalised in Task 5 — there must be exactly **one** install path, shared by tab selection and by deeplink.

**`Failed(raw, reason)`** — never navigates. Cold start needs no fallback destination because `MainActivity` already seeds `ShellRoute` and `ShellState` already defaults to the Settings tab *before* the link is processed; the default state **is** the fallback. So `Failed` only reports:

| Reason | Log | User message |
|---|---|---|
| `Malformed` | yes | no — nothing the user can act on |
| `UnknownFeature` / `NoResolver` | yes | yes — "this link needs a newer version of the app" |
| `Blocked(reason)` | yes | yes — show the guard's reason |
| `RedirectLoop` | yes | no — a developer configuration error |
| `InstallFailed` | yes | yes — "couldn't load that feature, try again" |

User messages go through `ShellEvent`, which is **currently empty** (it holds only a commented example). Add `ShellEvent.ShowMessage` — using the existing MVI one-shot event channel rather than inventing a new mechanism.

## Relevant Files & Context Pointers

- `shell/src/main/kotlin/com/danhdue/shell/ShellViewModel.kt` — **modify**, handle both commands
- `shell/src/main/kotlin/com/danhdue/shell/ShellEvent.kt` — **modify**, add `ShowMessage`
- `shell/src/main/kotlin/com/danhdue/shell/ShellScreen.kt` — **modify**, collect `event` and render (snackbar or equivalent)
- `shell/src/main/kotlin/com/danhdue/shell/ShellState.kt` — read; `installingModules` / `readyModules` from Task 5
- `packages/platform/src/main/kotlin/com/danhdue/platform/FeatureInstaller.kt` — read; `ensureInstalled` contract and `NoOpFeatureInstaller`
- `app/src/main/kotlin/com/danhdue/androiddigitalwallet/split/FeatureInstallerImpl.kt` — read; real terminal states and how failures surface
- `packages/framework/src/main/java/com/danhdue/framework/base/mvi/MviViewModel.kt` — read; `sendEvent` / `event`
- `shell/src/test/kotlin/com/danhdue/shell/ShellViewModelTest.kt` — **extend**

## Design Rationale

- **One install path.** Tab selection and `EnsureModule` both call the same private function. Two paths would drift, and `FeatureInstallerImpl` is already subtle (session-id filtering, listener unregistration on every terminal state).
- **The replay cap lives in the router, not here.** `:shell` dispatches the replay unconditionally; `DefaultDeepLinkRouter` (Task 4) tracks which modules it has already emitted `EnsureModule` for and refuses a second round. Keeping the loop guard in one place means the shell cannot accidentally bypass it.
- **`Failed` never navigating is a design decision, not an omission.** Recorded here so a future reader does not "fix" it by adding a jump to Home — which would yank a user out of whatever they were doing when a stale link arrives on a warm app.
- **Messages are localised through the existing mechanism.** Use `appStringResource` as `ShellScreen` already does for tab labels; do not hard-code English strings.
- **Never surface a raw `FailureReason` name to the user.** Log the technical reason, show a human sentence.

Applicable skills: `.agents/skills/test-driven-development`; `.agents/skills/quality_check` at the end.

## TDD Checklist

- [ ] **RED**: extend `ShellViewModelTest` with a fake `FeatureInstaller` and fake router. Failing first:
  - `EnsureModule` adds the module to `installingModules`
  - on install success: `readyModules` contains it, `installingModules` does not, and `dispatch(replay)` was called exactly once
  - on install failure: `installingModules` is cleared and a `ShowMessage` event is emitted; `readyModules` unchanged
  - selecting the Scanner tab still installs via the same path (Task 5 regression stays green)
  - `Failed(Malformed)` emits **no** `ShowMessage` and does not navigate
  - `Failed(NoResolver)` emits a `ShowMessage` and does not navigate
  - `Failed(Blocked("..."))` emits a `ShowMessage` carrying the guard's reason
  - `Failed(RedirectLoop)` emits no `ShowMessage`
  - no `Failed` variant mutates any back stack — assert all three stacks are identical before and after
- [ ] **GREEN**: implement both handlers and `ShellEvent.ShowMessage`; render it in `ShellScreen`.
- [ ] **REFACTOR**: express the reason-to-message mapping as one exhaustive `when` over `FailureReason`, so adding a reason later is a compile error rather than a silent omission.

## Definition of Done

- [ ] `./gradlew :shell:testDebugUnitTest` green, all cases above.
- [ ] Exactly one install code path exists — verified by inspection; tab selection and `EnsureModule` share it.
- [ ] The `FailureReason` mapping is an exhaustive `when` with no `else` branch.
- [ ] User-facing strings go through `appStringResource`; no hard-coded literals.
- [ ] `./gradlew :konsist-test:test detekt spotlessCheck assembleDebug bundleDebug` green.
- [ ] Manual: `dispatch("myapp://scanner")` with the split absent shows the spinner and then opens Scanner.

## Dependencies & Blockers

- Blocked by [Task 6](task_6_shell_execute_placement.md).
- Blocks [Task 12](task_12_scanner_dfm_resolver.md) — the DFM acceptance run needs this branch.

## References & Rollback

- Source spec §7.2, §7.3, §7.4 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- Epic HLD §4.5 — the "Failed never navigates" rationale
- **Rollback**: drop the two command branches and revert `ShellEvent`. Task 6's placement handling stays functional, so internal deeplinks to install-time features keep working.
