---
id: "task_8_scheme_build_placeholders"
status: "done"
priority: "high"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-10T05:01:00Z"
completedAt: "2026-09-10T05:01:00Z"
labels: ["infra", "template"]
order: "a8"
---

# Task 8: Scheme and Host Build Placeholders plus Rename Script

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

This is a reusable template. If the deeplink scheme is written literally into `AndroidManifest.xml`, everyone who clones the template ships an app answering to `myapp://` — colliding with every other clone on the same device.

So the scheme and the App Links host become build configuration:

- `AppConfig.deepLinkScheme` (template default: `myapp`)
- `AppConfig.appLinkHost` (template default: `app.example.com`)

Both are exposed to the manifest via `manifestPlaceholders`, so Task 9 can write `${deepLinkScheme}` / `${appLinkHost}` rather than literals.

`scripts/rename_project.sh` must rewrite both alongside the `applicationId` and `namespace` it already handles, so a clone gets its own scheme from the one command it already runs.

**This task must land before Task 9** — the manifest cannot reference placeholders that do not exist yet.

## Relevant Files & Context Pointers

- `buildSrc/src/main/kotlin/AppConfig.kt` — **modify**, add the two constants
- `app/build.gradle.kts` — **modify**, wire `manifestPlaceholders`
- `scripts/rename_project.sh` — **modify**, rewrite both values
- `build-logic/` and `buildSrc/src/main/kotlin/commons/` — read; confirm whether an existing convention plugin is a better home for the placeholder wiring than `app/build.gradle.kts`
- `.devtool/epic/android_super_app_template/task_15_rename_script_and_docs.md` — read; the established conventions and guard rails of the rename script

## Design Rationale

- **`manifestPlaceholders` over `resValue` or a raw string resource.** Placeholders are substituted at manifest-merge time, which is what an `intent-filter` `<data>` element needs — a string resource cannot be referenced from `android:scheme` in a way the App Links verifier can read at install time.
- **`app.example.com` as the default host, not a real domain.** `example.com` is IANA-reserved for documentation, so the template can never accidentally point at somebody's live site. The default will not verify as an App Link, which is correct and expected: the template ships no `assetlinks.json`, and Task 15 documents that.
- **The rename script is the single entry point after cloning.** Adding these two rewrites keeps that promise intact; a clone should never need to hand-edit a manifest.
- **The script must guard against partial rewrites** — follow the existing conventions (clean-tree check, `--force`, and the closing `./gradlew` verification step) rather than inventing new ones.

Applicable skill: `.agents/skills/quality_check` at the end, per `CRITICAL_RULES.md`.

## TDD Checklist

**TDD Adaptation, stated explicitly**: this task changes build configuration and a shell script. There is no Kotlin behaviour to drive with a unit test, and the project has no shell-test harness. RED/GREEN/REFACTOR is replaced by a concrete change list plus an executable verification that the substitution actually reached the merged manifest:

- [x] **Change**: add `deepLinkScheme` and `appLinkHost` to `AppConfig` with KDoc explaining they are template defaults meant to be rewritten by `rename_project.sh`.
- [x] **Change**: wire both into `manifestPlaceholders` for all build types.
- [x] **Change**: extend `scripts/rename_project.sh` to rewrite both, following its existing clean-tree / `--force` / verify conventions.
- [x] **Verify (substitution reaches the manifest)**: run `./gradlew :app:processDebugManifest` and inspect the merged manifest under `app/build/intermediates/merged_manifest*/` — confirm the placeholder tokens are gone and the literal values are present. Task 9 depends on this working.
- [x] **Verify (rename script)**: in a scratch clone or worktree, run `rename_project.sh` with a test name, then `grep` for the old scheme — zero occurrences — and run `./gradlew assembleDebug`.

## Definition of Done

- [x] `AppConfig` exposes both constants with KDoc.
- [x] `./gradlew :app:processDebugManifest` produces a merged manifest with no unresolved placeholder tokens.
- [x] `rename_project.sh` rewrites both values; a scratch-clone run leaves zero occurrences of the old scheme and builds green.
- [x] `./gradlew assembleDebug bundleDebug detekt spotlessCheck` green.
- [x] The default host is a documentation-reserved domain, never a real one.

## Dependencies & Blockers

- Blocked by: nothing — independent of Phases 1 and 2, may run in parallel.
- Blocks [Task 9](task_9_manifest_intent_filters.md). **Strict ordering**: the manifest cannot reference placeholders that do not exist.

## References & Rollback

- Source spec §9.4 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- [RFC 2606](https://www.rfc-editor.org/rfc/rfc2606) — reserved documentation domains
- **Rollback**: revert the three edits. Nothing consumes the placeholders until Task 9, so the revert is inert.
