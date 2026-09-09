---
id: "task_4_deeplink_router_pipeline"
status: "todo"
priority: "high"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-09T20:50:45Z"
completedAt: null
labels: ["architecture", "feature", "platform"]
order: "a4"
---

# Task 4: DefaultDeepLinkRouter Pipeline

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

Assemble Tasks 1–3 into the engine. This is the largest task in Phase 1 and the one that carries the epic's behaviour.

```kotlin
interface DeepLinkRouter {
    fun dispatch(uri: String)
    val commands: Flow<NavigationCommand>
}
```

Pipeline, in order (source spec §6.3):

1. parse → `null` ⇒ `Failed(Malformed)`
2. tier-1 lookup by `link.feature` → miss ⇒ `Failed(UnknownFeature)`
3. **pre-gate**: only when `entryPoint.requiresAuth`, run the guard chain against a target synthesised from tier 1
4. `entryPoint.dynamicModule != null` and not yet attempted ⇒ emit `EnsureModule(module, replay = uri)` and stop this pass
5. ask resolvers in turn (Hilt set, then `ServiceLoader`), first non-null wins → none ⇒ `Failed(NoResolver)`
6. full guard chain sorted by `order`: `Allow` continues, `Redirect` re-dispatches (**depth cap 3**, then `Failed(RedirectLoop)`), `Block` ⇒ `Failed(Blocked)`
7. derive `placement` when null — `InTab(tab, parents = listOf(entryRoute))`, or `RootFullScreen` when `tab == null`
8. emit the `NavigationCommand`

Also: subscribe to `AppEvent.UserLoggedIn` and replay any pending link.

**Two loop guards are mandatory, not optional hardening:**

- **Redirect depth cap 3** — guard A redirects to B, B back to A.
- **One replay per module** — the router keeps a `Set<String>` of modules it has already emitted `EnsureModule` for. On the replay pass that module is skipped at step 4 and falls through to step 5; if no resolver appeared, `Failed(InstallFailed)`. Without this, a `readyModules` update failure becomes an infinite install→replay loop.

**Command delivery uses `Channel(BUFFERED)`, not `AppEventBus`.** On cold start the link is dispatched from `MainActivity.onCreate`, before `ShellViewModel` exists. `AppEventBus` is `SharedFlow(replay = 0)` and would silently drop it. The `Channel` buffers until the single consumer attaches, and delivers once — so a configuration change does not re-execute the navigation.

## Relevant Files & Context Pointers

- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/DeepLinkRouter.kt` — **new** (interface)
- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/DefaultDeepLinkRouter.kt` — **new**, `internal`
- `packages/platform/src/main/kotlin/com/danhdue/platform/di/PlatformModule.kt` — **modify**, provide the router as `@Singleton`
- `packages/platform/src/main/kotlin/com/danhdue/platform/FeatureEntry.kt` — read; `ServiceLoader` resolver discovery
- `shell/src/main/kotlin/com/danhdue/shell/ShellScreen.kt` — **read the `installersFrom` / `hasNextOrStop` / `nextOrNull` helpers**; the same `ServiceConfigurationError` skip-don't-fail pattern is required here and must not be reinvented differently
- `packages/platform/src/main/kotlin/com/danhdue/platform/AppEventBus.kt` — read; `UserLoggedIn` subscription
- `packages/platform/src/test/kotlin/com/danhdue/platform/deeplink/DefaultDeepLinkRouterTest.kt` — **new**

## Design Rationale

- **Pre-gate before the split download.** Running auth only after resolution would make an unauthenticated user download a whole dynamic feature split and *then* be bounced to login. The cost is that the chain runs twice, which is exactly why Task 3 makes idempotency a contract obligation.
- **`ServiceLoader` iteration must skip, not fail.** The aggregated `META-INF/services/...FeatureEntry` file in `:app` lists **every** on-demand `FeatureEntry`, including splits that are not installed, so `ServiceConfigurationError` from `next()` is the expected steady state. `ShellScreen.installersFrom` already solves this with hand-driven `hasNext()`/`next()`; reuse that exact shape here (consider extracting it to `:platform` so both call sites share one implementation).
- **`internal` implementation behind a public interface** — consistent with Task 3 and the correction of finding 3.2.
- **`@Singleton`, not activity-scoped.** Links arrive from the `Application`/`Activity` layer before any ViewModel exists, and the pending-link replay must survive an Activity recreation.
- **The router never navigates.** It only emits commands. All back-stack mutation lives in `:shell` (Task 6/7), because that is where the back stacks are.

Applicable skills: `.agents/skills/test-driven-development`; `.agents/skills/systematic-debugging` if the `ServiceLoader` branch misbehaves; `.agents/skills/quality_check` at the end.

## TDD Checklist

- [ ] **RED**: `DefaultDeepLinkRouterTest` with fake resolvers, fake guards, a fake store and a `TestScope`. All failing first:
  - malformed URI ⇒ `Failed(Malformed)`
  - unknown feature key ⇒ `Failed(UnknownFeature)`
  - known feature, no resolver claims it ⇒ `Failed(NoResolver)`
  - happy path, `placement = null`, `tab = 2` ⇒ `OpenInTab(2, [entryRoute, destination])`
  - happy path, `placement = null`, `tab = null` ⇒ `OpenFullScreen(destination)`
  - explicit `placement` overrides the derived default
  - guards run in `order` sequence; a `Block` short-circuits and later guards are not called
  - `Redirect` re-dispatches; a 3-cycle redirect loop terminates with `Failed(RedirectLoop)`
  - `dynamicModule` not ready ⇒ `EnsureModule` emitted; replay of the same URI does **not** emit `EnsureModule` a second time
  - replay with still no resolver ⇒ `Failed(InstallFailed)`
  - pre-gate fires **before** `EnsureModule` when `entryPoint.requiresAuth` and the user is signed out
  - `AppEvent.UserLoggedIn` replays the pending link exactly once
  - **cold-start buffering**: `dispatch` before anyone collects `commands`, then collect — the command still arrives
- [ ] **GREEN**: Implement `DefaultDeepLinkRouter`.
- [ ] **REFACTOR**: Extract each pipeline stage into a named private function so the `dispatch` body reads as the numbered list above. Extract the shared `ServiceLoader` skip helper if it is duplicated with `ShellScreen`.

## Definition of Done

- [ ] `./gradlew :packages:platform:testDebugUnitTest` green, every case above covered.
- [ ] Both loop guards (redirect depth, replay-once) have a dedicated failing-first test.
- [ ] `DefaultDeepLinkRouter` is `internal`; only `DeepLinkRouter` is public.
- [ ] The `ServiceLoader` branch skips unloadable entries rather than throwing — asserted by a test with a deliberately bad service entry.
- [ ] `./gradlew detekt spotlessCheck assembleDebug` green; app behaviour unchanged (no consumer yet).

## Dependencies & Blockers

- Blocked by [Task 1](task_1_deeplink_parser.md), [Task 2](task_2_deeplink_contract.md), [Task 3](task_3_deeplink_guard_chain.md).
- Blocks [Task 6](task_6_shell_execute_placement.md), [Task 7](task_7_shell_ensure_module_and_failure.md), [Task 10](task_10_mainactivity_intent_handling.md).

## References & Rollback

- Source spec §5.6, §6.3, §6.4 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- Epic HLD §4.4 sequence diagram — the full pipeline walked end to end
- `shell/src/main/kotlin/com/danhdue/shell/ShellScreen.kt` — the existing `ServiceLoader` skip pattern
- **Rollback**: remove the router provider from `PlatformModule` and delete the two files. Phase 1 leaves no caller, so the revert is contained within `:platform`.
