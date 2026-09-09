---
id: "task_9_manifest_intent_filters"
status: "done"
priority: "high"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-10T05:01:50Z"
completedAt: "2026-09-10T05:01:50Z"
labels: ["feature", "app", "security"]
order: "a9"
---

# Task 9: Manifest Intent Filters and singleTop

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

Open the external entry point. `MainActivity` currently declares only `MAIN` / `LAUNCHER`. Add:

- an `ACTION_VIEW` filter for the custom scheme (`${deepLinkScheme}://`)
- an `ACTION_VIEW` filter for App Links (`https://${appLinkHost}`) with `android:autoVerify="true"`
- `android:launchMode="singleTop"`

**Why `singleTop` is not optional.** `MainActivity` is `standard` today. A deeplink arriving at a running app makes Android build a **second `MainActivity` instance** on top of the task — two `Navigator` instances (`@ActivityRetainedScoped`), two back stacks, split state. `singleTop` delivers `onNewIntent` to the existing instance instead.

`singleTop` rather than `singleTask`: this is a single-Activity app, so `singleTop` is sufficient, and it avoids `singleTask`'s task-affinity and stack-clearing side effects.

Keep the two `ACTION_VIEW` filters as **separate `<intent-filter>` elements**. Merging schemes and hosts into one filter makes Android's matching rules combinatorial — it would also match `myapp://app.example.com` and `https://` with no host constraint.

**This task makes `MainActivity` exported to arbitrary URIs from any app on the device.** That is the single riskiest change in the epic, which is why it lands in Phase 3, after the engine it feeds is fully tested.

## Relevant Files & Context Pointers

- `app/src/main/AndroidManifest.xml` — **modify**, the `MainActivity` declaration
- `app/build.gradle.kts` — read; the `manifestPlaceholders` added in Task 8
- `buildSrc/src/main/kotlin/AppConfig.kt` — read; the placeholder values
- `features/scanner/src/main/AndroidManifest.xml` — read; house style for manifest comments
- `app/src/main/kotlin/com/danhdue/androiddigitalwallet/ui/MainActivity.kt` — read only; Task 10 changes the Kotlin side

## Design Rationale

- **Separate filters per scheme.** Android's intent matching combines every `<data>` child within a single filter, so one merged filter silently widens the match set. Two explicit filters state exactly two accepted shapes.
- **`autoVerify="true"` on the https filter only.** Custom schemes cannot be verified — any app may claim them. Marking https for verification is what makes it the trustworthy channel described in HLD §4.6.
- **`android:exported="true"` stays as-is** — already required by the `LAUNCHER` filter; this task does not change the exported flag, only widens what the exported Activity accepts. Say so in the manifest comment so a reviewer is not misled.
- **A manifest comment must record the security posture**, matching how `features/scanner/src/main/AndroidManifest.xml` documents its `dist:module` block: sensitive links belong on https, params are untrusted.

Applicable skill: `.agents/skills/quality_check` at the end.

## TDD Checklist

**TDD Adaptation, stated explicitly**: manifest XML has no unit-testable behaviour in this project (no Robolectric, and instrumentation tests would need a device in CI, which the pipeline deliberately avoids). RED/GREEN/REFACTOR is replaced by a change list plus `adb`-driven verification against a real installed build:

- [x] **Change**: add the custom-scheme `ACTION_VIEW` filter with `DEFAULT` and `BROWSABLE` categories.
- [x] **Change**: add the App Links filter with `android:autoVerify="true"`, `DEFAULT` + `BROWSABLE`, `android:scheme="https"`, `android:host="${appLinkHost}"`.
- [x] **Change**: set `android:launchMode="singleTop"`.
- [x] **Change**: add the security-posture comment block.
- [x] **Verify (merged manifest)**: `./gradlew :app:processDebugManifest`, then inspect the merged output — both filters present, placeholders substituted, `singleTop` applied.
- [x] **Verify (resolution)**: install, then `adb shell am start -W -a android.intent.action.VIEW -d "myapp://settings"` resolves to `MainActivity`. The app need not navigate yet — Task 10 wires the Kotlin side — but the Intent must reach the Activity.
- [x] **Verify (no duplicate instance)**: with the app already running, fire the same command and confirm via `adb shell dumpsys activity activities` that only one `MainActivity` instance exists.
- [x] **Verify (App Links status)**: `adb shell pm get-app-links <applicationId>` — expected to report unverified for `app.example.com`, which is the documented correct outcome for the template.

## Definition of Done

- [x] Merged manifest contains both filters with substituted values and `singleTop`.
- [x] `adb` resolution check passes for the custom scheme.
- [x] Only one `MainActivity` instance after a warm-start deeplink.
- [x] The manifest carries a comment recording the security posture (untrusted params; sensitive links over https only).
- [x] `./gradlew assembleDebug bundleDebug detekt spotlessCheck :konsist-test:test` green.
- [x] Manual regression: launcher icon still opens the app normally.

## Dependencies & Blockers

- Blocked by [Task 8](task_8_scheme_build_placeholders.md) — **strictly**; the placeholders must exist first.
- Blocks [Task 10](task_10_mainactivity_intent_handling.md).

## References & Rollback

- Source spec §6.1, §6.2, §8 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- Epic HLD §4.6 — attack surface
- [Android App Links verification](https://developer.android.com/training/app-links/verify-android-applinks)
- **Rollback**: delete the two `intent-filter` blocks and the `launchMode` attribute. This **fully closes the external attack surface** while leaving the engine intact and usable for internal navigation — the cleanest revert boundary in the epic.
