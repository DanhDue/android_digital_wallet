---
id: "task_15_docs_and_e2e"
status: "todo"
priority: "medium"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-09T20:50:45Z"
completedAt: null
labels: ["documentation", "acceptance"]
order: "a15"
---

# Task 15: Documentation and E2E Acceptance

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

Close the epic: document the engine for people who clone the template, and prove every goal end to end.

### Documentation

`docs/architecture/ARCHITECTURE.md` gains a DeepLink section, structured like the existing cross-feature-communication material:

- the two tiers and who owns which
- how to add a link to an existing feature (the common case — one resolver edit)
- how to add a link to a new feature (the brick does it)
- `Placement` and when to override the default
- how to add a guard
- the four link sources and how each reaches the router

A security subsection carrying HLD §4.6 verbatim in substance: params are untrusted; custom schemes are hijackable so sensitive links go over https; `requiresAuth` is defence in depth, not authorisation; the allowlist property.

An App Links setup guide: what `assetlinks.json` must contain, where it goes, how `rename_project.sh` sets the host, and `adb shell pm get-app-links <pkg>` to check — including that the template default `app.example.com` will **not** verify, which is expected.

A push-notification note: the template ships `DeepLinkIntentFactory` but no `FirebaseMessagingService`, with a sketch of how to wire one.

### E2E acceptance

Run the full matrix on a device or emulator and record the results in the epic HLD.

## Relevant Files & Context Pointers

- `docs/architecture/ARCHITECTURE.md` — **modify**; the main deeplink section
- `ARCHITECTURE.md` — **modify**; the root pointer file's table of contents
- `README.md` — **modify**; mention deeplink support in the template feature list
- `AGENTS.md`, `PROJECT_RULES.md` — **check** whether they need a deeplink convention line
- `docs/` — **new**, an App Links setup guide
- `.devtool/epic/deeplink_router_engine/deeplink_router_engine.en.md` and `.vi.md` — **modify**; final status and the recorded acceptance results
- `.github/workflows/ci.yml` — read; confirm no CI change was needed, as the design predicted
- `scripts/rename_project.sh` — read; referenced by the setup guide

## Design Rationale

- **The deeplink section goes beside cross-feature communication, not in a standalone file.** `AppRoutes` + `AppEventBus` + `EntryProviderInstaller` are already documented together as the ways modules talk without importing each other; deeplinking is the fourth and belongs in that company.
- **Security documentation is not optional for a wallet template.** An exported `intent-filter` accepting arbitrary URIs is a real surface, and a clone will inherit it without having read this epic. The warning has to live where they will find it.
- **Both HLD language variants must be updated together.** The `.en.md` is canonical and written first; the `.vi.md` must not diverge in structure or fact.
- **Record acceptance results in the HLD, not only in a PR description.** PR bodies are hard to find a year later; the epic directory is where someone looks.

Applicable skills: `.agents/skills/verification-before-completion` — this task makes completion claims, so every claim needs command output behind it. `.agents/skills/quality_check` at the end.

## TDD Checklist

**TDD Adaptation, stated explicitly**: this task produces documentation and runs an acceptance matrix; there is no new production behaviour to drive with a failing test. RED/GREEN/REFACTOR is replaced by the acceptance matrix below, where each row is a verifiable pass/fail against a real build — a stricter gate than a unit test, since it exercises the assembled system.

**Acceptance matrix** — every row must pass and be recorded:

- [ ] **D2 / custom scheme**: `adb shell am start -a android.intent.action.VIEW -d "myapp://settings/profile"` — cold start opens Profile in the Settings tab
- [ ] **D2 / App Links**: same via `https://app.example.com/settings/profile` — identical result
- [ ] **D2 / push**: a notification built with `DeepLinkIntentFactory` navigates on tap
- [ ] **D2 / internal**: `dispatch(...)` from code navigates
- [ ] **D3 / DFM**: `myapp://scanner` with the split absent installs it, shows progress, then opens
- [ ] **D6 / placement**: a link using the derived default synthesises `[parent, destination]`; a link with explicit `RootFullScreen` covers the shell
- [ ] **D7 / guard**: an auth-gated link while signed out stores the pending link, redirects to login, and replays after sign-in
- [ ] **Failure**: `myapp://nope` leaves the current screen intact and shows the "newer version" message
- [ ] **Rotation**: rotating after a deeplink does not re-fire it
- [ ] **Warm start**: a deeplink to a running app produces no second `MainActivity` instance
- [ ] **D5 / sandbox**: `./gradlew :features:settings:testDebugUnitTest` passes **run on its own**
- [ ] **D8 / template**: in a scratch worktree — `rename_project.sh acme_wallet com.acme.wallet`, then `mason make mvi_feature --name payments`, then `mason make mvi_feature --name kyc --delivery on-demand`, then `./gradlew :konsist-test:test assembleDebug bundleDebug` green, and `acme://payments` navigates
- [ ] **D1 / governance**: `./gradlew :konsist-test:test detekt spotlessCheck testDebugUnitTest assembleDebug bundleDebug` all green; `konsist_baseline.txt` and `konsist_boundary_whitelist.txt` both still empty

## Definition of Done

- [ ] Every acceptance row passes and the results are recorded in both HLD language variants.
- [ ] `docs/architecture/ARCHITECTURE.md` covers all six documentation points plus the security subsection.
- [ ] The App Links setup guide exists and states that the default host will not verify.
- [ ] The `.vi.md` HLD matches the `.en.md` in structure and facts.
- [ ] Epic status updated to reflect completion.
- [ ] **Criterion 2.1 confirmed fully met** — Central Router and URL Schema / DeepLink both present and demonstrated.
- [ ] CI needed no modification, as the design predicted — or, if it did, the deviation is documented in the HLD.

## Dependencies & Blockers

- Blocked by [Task 10](task_10_mainactivity_intent_handling.md), [Task 12](task_12_scanner_dfm_resolver.md), [Task 14](task_14_mason_brick_deeplink.md).
- Blocked by every preceding task; this is the epic's closing task.

## References & Rollback

- Source spec §8, §9 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- Epic HLD §4.6, §7
- `.devtool/epic/android_super_app_template/task_16_e2e_acceptance_validation.md` — the precedent acceptance task
- **Rollback**: documentation-only, so reverting affects no runtime behaviour. A failing acceptance row is not rolled back — it reopens the task that owns the failure.
