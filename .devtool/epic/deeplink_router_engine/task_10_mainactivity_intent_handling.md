---
id: "task_10_mainactivity_intent_handling"
status: "done"
priority: "high"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-10T09:14:20Z"
completedAt: "2026-09-10T09:14:20Z"
labels: ["feature", "app"]
order: "a10"
---

# Task 10: MainActivity Intent Handling and Push Factory

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

Connect the Intent that Task 9 made reachable to the router that Task 4 built.

- `onCreate` — read `intent.data`, dispatch (the **cold start** path)
- `onNewIntent` — read the new Intent's data, dispatch (the **warm start** path, reachable because of `singleTop`)

**The consumed-Intent problem.** `onCreate` runs again after a configuration change with the **same `Intent` object**, so a naive implementation re-fires the link on every rotation. Fix: after dispatching, set a marker extra on `getIntent()`. Mutating the retained Intent is what makes the marker survive the recreation. Check the marker before dispatching.

**Push notification support** — ship `DeepLinkIntentFactory.pendingIntent(context, uri)`, producing a `PendingIntent` that wraps the same `ACTION_VIEW` Intent. Push therefore travels the identical pipeline with no special branch.

Per the epic's Non-Goals: **no `FirebaseMessagingService`**. The repo deliberately has no `google-services.json` (CI runs secret-free). The factory plus documentation is the deliverable; consumers wire their own FCM.

At the end of this task **all four link sources are functional**.

## Relevant Files & Context Pointers

- `app/src/main/kotlin/com/danhdue/androiddigitalwallet/ui/MainActivity.kt` — **modify**, inject `DeepLinkRouter`, add `onNewIntent`, handle both paths
- `app/src/main/kotlin/com/danhdue/androiddigitalwallet/deeplink/DeepLinkIntentFactory.kt` — **new**
- `app/src/main/AndroidManifest.xml` — read; the filters from Task 9
- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/DeepLinkRouter.kt` — read
- `app/src/test/java/com/danhdue/androiddigitalwallet/split/FeatureInstallerImplTest.kt` — read; the module's existing unit-test conventions
- `app/src/test/java/com/danhdue/androiddigitalwallet/deeplink/DeepLinkIntentFactoryTest.kt` — **new**

## Design Rationale

- **A marker extra rather than `intent.data = null`.** Nulling the data works but destroys information a crash report would want. An additive boolean extra is non-destructive and states intent plainly.
- **`MainActivity` dispatches; it does not navigate.** It holds no back stack. Everything it does is `router.dispatch(uri)`; `ShellViewModel` executes. This keeps the "one executor" property established in Task 6.
- **The factory lives in `:app`, not `:platform`.** It needs `PendingIntent` and the concrete `MainActivity` class — both host concerns. `:platform` stays free of Activity references.
- **`PendingIntent` flags**: `FLAG_IMMUTABLE` is required from API 31 (`targetSdk` here is well past it) and is the correct choice anyway — nothing should let another component rewrite the deeplink after the fact.
- **Unique request codes per URI.** `PendingIntent` dedupes on Intent equality ignoring extras; without distinct request codes (or `FLAG_UPDATE_CURRENT`), two notifications with different deeplinks would collapse into one. Document the choice made.

Applicable skills: `.agents/skills/test-driven-development` for the factory; `.agents/skills/quality_check` at the end.

## TDD Checklist

- [x] **RED**: `DeepLinkIntentFactoryTest`, failing first (Intent construction is testable without a device using the project's existing unit-test setup):
  - the produced Intent has `ACTION_VIEW` and the given URI as data
  - it targets `MainActivity`
  - `FLAG_IMMUTABLE` is set
  - two different URIs yield distinct `PendingIntent`s, not a collapsed single one
- [x] **GREEN**: implement the factory.
- [x] **Non-TDD, verified on device** (`MainActivity` Intent plumbing needs a real Activity lifecycle; instrumentation in CI is out of scope for this pipeline):
  - `onCreate` cold start with `-d "myapp://settings/profile"` opens Profile inside the Settings tab, Back returns to Settings
  - warm start, app in Scanner tab, same command switches to Settings and opens Profile
  - **rotate after a deeplink — the link does not re-fire** (this is the marker-extra regression, the highest-value manual check in the task)
  - normal launcher start dispatches nothing
  - `https://app.example.com/settings/profile` via `adb` behaves identically to the custom scheme
  - an unknown link (`myapp://nope`) leaves the app on its current screen and shows the "needs a newer version" message
- [x] **REFACTOR**: extract the shared read-dispatch-mark logic so `onCreate` and `onNewIntent` share one function.

## Definition of Done

- [x] `./gradlew :app:testDebugUnitTest` green.
- [x] All six manual checks above pass on a device or emulator, **including the rotation check**.
- [x] `onCreate` and `onNewIntent` share one implementation, not two copies.
- [x] No `FirebaseMessagingService` was added (epic Non-Goal).
- [x] `./gradlew assembleDebug bundleDebug detekt spotlessCheck :konsist-test:test` green.
- [x] **All four link sources demonstrably work** — custom scheme, App Links, `PendingIntent`, internal `dispatch`.

## Dependencies & Blockers

- Blocked by [Task 9](task_9_manifest_intent_filters.md) — Intents must reach the Activity.
- Blocked by [Task 7](task_7_shell_ensure_module_and_failure.md) — the failure path must exist for the unknown-link check.
- Blocks [Task 15](task_15_docs_and_e2e.md).

## References & Rollback

- Source spec §6.1, §6.2 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- [PendingIntent mutability](https://developer.android.com/guide/components/intents-filters#DeclaringPendingIntent)
- **Rollback**: remove the dispatch calls and `onNewIntent`, delete the factory. Reverting Task 9 as well fully closes the external surface.
