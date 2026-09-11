---
id: "task_1_configure_mode_script"
status: "done"
priority: "high"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T16:03:00Z"
completedAt: "2026-09-11T16:03:00Z"
labels: ["scripts", "tooling", "architecture"]
order: "a1"
---

# Task 1: Implement Mode Configuration Script (configure_mode.sh)

Epic: [tri_mode_and_flutter_plugin_devbed](../epic/tri_mode_and_flutter_plugin_devbed/tri_mode_and_flutter_plugin_devbed.en.md)

## Requirement Analysis
The Android template requires an automated, idempotent Bash script `scripts/configure_mode.sh` allowing developers to configure the project into one of three distinct profiles:
1. `enterprise`: All 12 modules included in `settings.gradle.kts`, DFM `:features:scanner` enabled in `app/build.gradle.kts`, Konsist test enabled, BCV active.
2. `lean`: 9 modules active (`:app`, `:shell`, `:packages:*`, `:features:settings`, `:libraries:testutils`). DFM `:features:scanner`, `:features:settings:sample`, `:konsist-test` commented out; BCV disabled.
3. `plugin`: Only `:plugin` and `:sample` active in `settings.gradle.kts`. All host app modules (`:app`, `:shell`, `:packages:*`, `:features:*`, `:konsist-test`) disabled.

Supports optional `--prune` flag to physically remove unneeded module folders when setting up a fresh repository.

## Relevant Files & Context Pointers
- `settings.gradle.kts`
- `app/build.gradle.kts`
- `build.gradle.kts`
- `scripts/configure_mode.sh` [NEW]
- `scripts/test_configure_mode.sh` [NEW]

## Design Rationale
- **Zero-Dependency CLI Tooling:** Shell scripting (`bash` with `set -Eeuo pipefail`) executes natively on macOS, Linux, and CI/CD without requiring Dart SDK, Python, or Mason CLI installations.
- **Idempotent Patching:** Regular expressions using Perl one-liners guarantee safe in-place replacement and ensure running the script multiple times produces zero unintended diffs.
- **Applicable Skills:** `systematic-debugging` (for shell failure triage).

### BDD SCENARIOS

#### Scenario 1: Switch to Lean Mode (Happy Path)
```gherkin
Given the project is in default "enterprise" mode with all 12 modules active
When the developer executes "./scripts/configure_mode.sh lean"
Then the script exits with code 0
And "settings.gradle.kts" has ":features:scanner", ":features:settings:sample", and ":konsist-test" commented out
And "app/build.gradle.kts" has ":features:scanner" removed from "dynamicFeatures"
And root "build.gradle.kts" has Binary Compatibility Validator plugin commented out
And "./gradlew projects" resolves exactly 9 active modules
```

#### Scenario 2: Switch to Plugin Mode (Happy Path)
```gherkin
Given the project is in "enterprise" mode
When the developer executes "./scripts/configure_mode.sh plugin"
Then the script exits with code 0
And "settings.gradle.kts" contains only ":plugin" and ":sample" active
And all host modules (":app", ":shell", ":packages:*", ":features:*", ":konsist-test") are disabled
And "./gradlew projects" resolves exactly 2 active modules
```

#### Scenario 3: Invalid Mode Argument (Edge Cases & Boundaries)
```gherkin
Given the project is in a clean git state
When the developer executes "./scripts/configure_mode.sh invalid_mode"
Then the script exits with code 1
And outputs "error: invalid mode 'invalid_mode'. Valid modes are: enterprise, lean, plugin"
And leaves all files unmodified
```

#### Scenario 4: Round-trip State Transitions & Idempotency (State Transitions)
```gherkin
Given the project has been switched to "lean" mode
When the developer executes "./scripts/configure_mode.sh lean" again
Then the script exits with code 0 and makes zero additional changes
When the developer executes "./scripts/configure_mode.sh enterprise"
Then all 12 modules are restored cleanly without duplicate comment markers
And "./gradlew :konsist-test:test" runs successfully
```

#### Scenario 5: Rapid Successive Execution (Async / Race Conditions)
```gherkin
Given multiple sequential invocations of configure_mode.sh
When switching between "enterprise", "lean", and "plugin" back-to-back in a tight loop
Then no temporary files (e.g. "*.tmp") are left behind
And final file permissions (executable bit) on scripts remain 0755
```

#### Scenario 6: Prune Flag with Unclean Working Tree (Failures & Resilience)
```gherkin
Given the git working tree has uncommitted modifications
When the developer executes "./scripts/configure_mode.sh lean --prune" without "--force"
Then the script warns the user about uncommitted changes
And aborts before deleting any physical directories
```

### TDD Checklist (The Dev Persona)
*TDD Adaptation:* Shell script automation. Verified via automated test suite `scripts/test_configure_mode.sh` operating on fixture copies of Gradle files.

- [x] **RED**: Write `scripts/test_configure_mode.sh` containing test functions for Scenarios 1–6 asserting failure against uncreated script.
- [x] **GREEN**: Implement `scripts/configure_mode.sh` with robust regex handling for all 3 modes and `--prune` safeguards. Run `scripts/test_configure_mode.sh` to confirm all assertions pass.
- [x] **REFACTOR**: Standardize error messaging, verify macOS BSD vs Linux GNU compatibility, and format script with `shfmt` if available.

## Definition of Done (DoD)
- `scripts/configure_mode.sh` executable exists with `chmod +x`.
- `scripts/test_configure_mode.sh` passes 100% of BDD test assertions.
- Live project compiles and resolves modules cleanly in each mode via `./gradlew projects`.

## Dependencies & Blockers
- Blocked by: None.

## References & Rollback
- Reference: Spec Section 5.2.
- Rollback: `git checkout HEAD -- settings.gradle.kts app/build.gradle.kts build.gradle.kts && rm -f scripts/configure_mode.sh scripts/test_configure_mode.sh`.
