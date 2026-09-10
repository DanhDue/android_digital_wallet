---
id: "task_14_mason_brick_deeplink"
status: "done"
priority: "medium"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-10T09:31:00Z"
completedAt: "2026-09-10T09:31:00Z"
labels: ["template", "tooling", "mason"]
order: "a14"
---

# Task 14: Mason Brick Wiring for DeepLinks

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

Goal **D8**: a generated feature must arrive with a working deeplink, already wired. Without this, every new feature needs manual wiring and the engine decays into something only its authors use.

`mvi_feature`'s `post_gen` hook already wires `settings.gradle.kts`, `AppRoutes`, the Hilt `@IntoSet` nav module, and the `:app`/`:shell` dependency lines. Extend the same hook:

- generate `{{Name}}DeepLinkResolver` in `presentation/di` (the location K10 requires), resolving the feature's root screen
- append one `FeatureEntryPoint` line to `AppDeepLinks.entryPoints` — the same append mechanism already used for `AppRoutes`
- `--delivery on-demand`: contribute via `FeatureEntry.resolver()` instead of Hilt `@IntoSet`, and set `dynamicModule = "<name>"` on the entry-point line
- `remove_feature`: strip the entry-point line

**No new brick variable.** Deeplink support is the default, not an option — a feature that does not want one deletes the generated resolver. Adding a `deepLink` flag would let people generate features that silently sit outside the URL surface.

The `tab` value is the open question the hook cannot answer on its own: a generated feature is not automatically a shell tab. Default to `tab = null` (which yields `RootFullScreen` placement) and emit a clearly-marked TODO comment on the generated line telling the developer to set a tab index if the feature becomes a tab. State this in the brick README.

## Relevant Files & Context Pointers

- `bricks/mvi_feature/hooks/post_gen.dart` — **modify**; 1031 lines, already contains the `AppRoutes` append logic to mirror
- `bricks/mvi_feature/brick.yaml` — read; the existing `delivery` variable, no new variable needed
- `bricks/mvi_feature/__brick__/features/{{name.snakeCase()}}/src/main/kotlin/.../presentation/` — **add** the resolver template
- `bricks/remove_feature/hooks/` — **modify**; remove the entry-point line
- `bricks/mvi_feature/README.md`, `bricks/README.md` — **modify**; document the behaviour and the `tab` TODO
- `features/settings/.../di/SettingsDeepLinkResolver.kt` — the install-time shape to template from (Task 11)
- `features/scanner/.../di/ScannerFeatureEntry.kt` — the on-demand shape to template from (Task 12)
- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/AppDeepLinks.kt` — the append target
- `.devtool/epic/android_super_app_template/task_4_mason_brick_wiring.md` — read; established hook conventions

## Design Rationale

- **Reuse the existing append machinery.** `post_gen.dart` already edits `AppRoutes` by locating an anchor and inserting a line. Use the same approach for `AppDeepLinks` rather than a second, differently-shaped file editor — one style of file surgery is enough for a maintainer to learn.
- **Idempotence matters.** Running the brick twice with the same name must not duplicate the entry-point line. Follow whatever guard the existing `AppRoutes` append uses; if it has none, this is the moment to add one for both.
- **`remove_feature` must stay symmetric.** The predecessor epic's pain was wiring that could be added but not cleanly removed. Every insertion point this task adds gets a matching removal.
- **`tab = null` plus a visible TODO, not a guess.** Silently defaulting to a real tab index would produce a link that hijacks an unrelated tab — a bug that only shows up at runtime. `RootFullScreen` is always safe.

Applicable skills: `.agents/skills/quality_check` at the end. Read `.devtool/epic/android_super_app_template/task_4_mason_brick_wiring.md` for hook conventions before editing.

## TDD Checklist

**TDD Adaptation, stated explicitly**: the hook is a Dart code generator with no test harness in this repo, and the predecessor epic's brick tasks established acceptance-by-generation as the convention. RED/GREEN/REFACTOR is replaced by generate-and-verify runs against a scratch worktree, which exercise the real output rather than a mock of it:

- [x] **Change**: add the resolver template and the `AppDeepLinks` append (both delivery modes) to `post_gen.dart`.
- [x] **Change**: add the entry-point removal to `remove_feature`.
- [x] **Change**: document both in the brick READMEs, including the `tab = null` TODO.
- [x] **Verify (install-time)**: in a scratch worktree, `mason make mvi_feature --name payments`, then `./gradlew :features:payments:testDebugUnitTest :konsist-test:test assembleDebug` — all green, K10 passes on the generated resolver, and `myapp://payments` navigates.
- [x] **Verify (on-demand)**: `mason make mvi_feature --name kyc --delivery on-demand`, then `./gradlew :konsist-test:test assembleDebug bundleDebug` green, with `dynamicModule = "kyc"` present on the generated entry-point line.
- [x] **Verify (idempotence)**: run the same generation twice; `AppDeepLinks` gains exactly one line, not two.
- [x] **Verify (removal)**: `mason make remove_feature --name payments`, then confirm zero residual references (`grep`) and `./gradlew assembleDebug` green.
- [x] **Verify (existing behaviour)**: the `settings.gradle.kts` / `AppRoutes` / nav-module wiring the hook already did still works — no regression.

## Definition of Done

- [x] Both generation modes produce a feature with a working deeplink and no manual edits.
- [x] The generated resolver satisfies K10 without adjustment.
- [x] Generation is idempotent.
- [x] `remove_feature` leaves zero residue, verified by `grep`.
- [x] Brick READMEs document the behaviour and the `tab` TODO.
- [x] No new brick variable was introduced.
- [x] All verification runs above are green.

## Dependencies & Blockers

- Blocked by [Task 11](task_11_settings_resolver.md) and [Task 12](task_12_scanner_dfm_resolver.md) — the two shapes being templated.
- Blocked by [Task 13](task_13_konsist_k10.md) — generated output must satisfy K10.
- Blocks [Task 15](task_15_docs_and_e2e.md) — the E2E acceptance drives the brick.

## References & Rollback

- Source spec §9.3 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- `.devtool/epic/android_super_app_template/task_4_mason_brick_wiring.md`
- **Rollback**: revert the hook edits. Existing features are unaffected — only newly generated ones would lose auto-wiring, and those can be wired by hand from the Task 11 exemplar.
