---
id: "task_1_configure_mode_script"
status: "todo"
priority: "high"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T15:20:00Z"
completedAt: null
labels: ["architecture", "tooling", "scripts"]
order: "a1"
---

# Task 1: Implement Mode Configuration Script (configure_mode.sh)

Epic: [tri_mode_and_flutter_plugin_devbed](../epic/tri_mode_and_flutter_plugin_devbed/tri_mode_and_flutter_plugin_devbed.en.md)

## Requirement Analysis
The Android template needs an automated, idempotent shell script `scripts/configure_mode.sh` allowing developers to switch between three modes:
1. `enterprise`: All 12 modules active, DFM `:features:scanner` enabled, Konsist K1–K10 enabled, BCV active.
2. `lean`: 9 modules active (`:app`, `:shell`, `:packages:*`, `:features:settings`, `:libraries:testutils`). DFM `:features:scanner`, `:features:settings:sample`, `:konsist-test` commented out; BCV disabled.
3. `plugin`: Only `:plugin` and `:sample` active in `settings.gradle.kts`. All host app modules (`:app`, `:shell`, `:packages:*`, `:features:*`, `:konsist-test`) disabled.

The script must support `--prune` to physically delete unneeded module folders when setting up a fresh repository, and must be idempotent (running twice produces no unintended diffs).

## Relevant Files & Context Pointers
- `settings.gradle.kts`
- `app/build.gradle.kts`
- `build.gradle.kts`
- `scripts/configure_mode.sh` [NEW]
- `scripts/test_configure_mode.sh` [NEW]

## Design Rationale
- **Shell-only automation:** Using Bash (`set -Eeuo pipefail`) ensures developers and CI/CD pipelines can configure project modes immediately upon cloning without requiring Dart SDK or Mason CLI.
- **Safe in-place modification:** Regex substitution using Perl/Sed with backup buffers guarantees file integrity if interrupted.
- **Skill recommendation:** Use standard shell scripting best practices and idempotent design.

## TDD Checklist
*TDD Adaptation:* This task develops a command-line automation script. TDD is applied via an automated test runner `scripts/test_configure_mode.sh` executing assertions on test fixture copies of `settings.gradle.kts` and `app/build.gradle.kts`.

- [ ] **RED**: Write `scripts/test_configure_mode.sh` verifying round-trip transitions (`enterprise` -> `lean` -> `plugin` -> `enterprise`) and `--prune` behavior against fixtures, asserting failure on missing script.
- [ ] **GREEN**: Implement `scripts/configure_mode.sh` with regex patch logic for `settings.gradle.kts`, `app/build.gradle.kts`, and root `build.gradle.kts`. Verify `scripts/test_configure_mode.sh` passes.
- [ ] **REFACTOR**: Ensure cross-platform compatibility (macOS BSD sed vs GNU sed via Perl one-liners), add usage help, and clean up temporary tags.

## Definition of Done (DoD)
- `scripts/configure_mode.sh <enterprise|lean|plugin> [--prune]` executes with exit code 0.
- Running `./scripts/test_configure_mode.sh` passes all round-trip test assertions.
- Live `settings.gradle.kts` builds correctly in each mode via `./gradlew projects`.

## Dependencies & Blockers
- Blocked by: None.

## References & Rollback
- Reference: Spec Section 5.2.
- Rollback: `git checkout HEAD -- settings.gradle.kts app/build.gradle.kts build.gradle.kts && rm -f scripts/configure_mode.sh scripts/test_configure_mode.sh`.
