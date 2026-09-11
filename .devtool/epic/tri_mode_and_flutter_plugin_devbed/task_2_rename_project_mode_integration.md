---
id: "task_2_rename_project_mode_integration"
status: "done"
priority: "medium"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T17:12:00Z"
completedAt: "2026-09-11T17:12:00Z"
labels: ["scripts", "tooling", "automation"]
order: "a2"
---

# Task 2: Integrate --mode Option into scripts/rename_project.sh

Epic: [tri_mode_and_flutter_plugin_devbed](../epic/tri_mode_and_flutter_plugin_devbed/tri_mode_and_flutter_plugin_devbed.en.md)

## Requirement Analysis
Extend `scripts/rename_project.sh` to accept a new CLI option `--mode <enterprise|lean|plugin>`:
- Default value is `enterprise` (preserving exact backward compatibility when omitted).
- Parse `--mode` during CLI argument parsing (supporting both `--mode value` and `--mode=value`).
- When running in `--dry-run`, print the planned mode configuration action without executing.
- During actual execution, execute physical package rename first, then call `scripts/configure_mode.sh <mode>` to configure the project mode.
- Update verification gate in `rename_project.sh` to adapt self-verify commands based on chosen mode (e.g. skip `:konsist-test:test` in `lean` and `plugin` modes).

## Relevant Files & Context Pointers
- `scripts/rename_project.sh`
- `scripts/configure_mode.sh`

## Design Rationale
- **Single post-clone entrypoint:** Developers cloning this template can perform project renaming and mode selection in a single command:
  `./scripts/rename_project.sh my_app com.acme.myapp "My App" --mode lean`
- **Dynamic self-verification:** Mode-aware verification prevents false failures when `:konsist-test` is disabled in lean or plugin mode.
- **Applicable Skills:** `systematic-debugging`.

### BDD SCENARIOS

#### Scenario 1: Rename with Lean Mode (Happy Path)
```gherkin
Given a clean checkout of the template
When the developer runs "./scripts/rename_project.sh acme_app com.acme.wallet --mode lean --force"
Then the package directory tree is moved to "com/acme/wallet"
And rootProject.name is renamed to "AcmeApp"
And "scripts/configure_mode.sh lean" is executed automatically
And self-verification runs "./gradlew assembleDebug" without attempting to run ":konsist-test"
And the command exits with code 0
```

#### Scenario 2: Rename with Plugin Mode (Happy Path)
```gherkin
Given a clean checkout of the template
When the developer runs "./scripts/rename_project.sh my_plugin com.acme.plugin --mode plugin --force"
Then package declarations are updated to "com.acme.plugin"
And "scripts/configure_mode.sh plugin" is executed
And only ":plugin" and ":sample" are compiled during self-verify
And the command exits with code 0
```

#### Scenario 3: Rename Dry-Run with Mode Flag (Edge Cases & Boundaries)
```gherkin
Given any checkout of the template
When the developer runs "./scripts/rename_project.sh test_app com.test.app --mode lean --dry-run"
Then the output displays "MODE ............... lean"
And no files on disk are modified
And git status remains clean
```

#### Scenario 4: Invalid Mode Argument to Rename Script (Edge Cases & Boundaries)
```gherkin
When the developer runs "./scripts/rename_project.sh test_app com.test.app --mode unknown"
Then the script exits with code 1
And prints "error: unknown mode 'unknown'. Valid modes are: enterprise, lean, plugin"
And aborts before renaming any files or folders
```

#### Scenario 5: Verification Gate Failure Recovery (Failures & Resilience)
```gherkin
Given a rename operation where compilation fails in self-verify
When the ERR trap is triggered
Then the script prints the recovery hint "git reset --hard HEAD && git clean -fd"
And exits with non-zero exit code
```

### TDD Checklist (The Dev Persona)
*TDD Adaptation:* CLI option parsing and integration script task. Verified using dry-run tests and argument parsing assertions.

- [ ] **RED**: Add test assertions in a test script checking that `--mode` is rejected if invalid, and that `--dry-run` outputs the planned mode.
- [ ] **GREEN**: Update argument parsing in `scripts/rename_project.sh` to accept `--mode`, validate it, display it in the plan, invoke `scripts/configure_mode.sh`, and adapt the self-verify command.
- [ ] **REFACTOR**: Ensure formatting with `shfmt` and document the new parameter in `--help`.

## Definition of Done (DoD)
- `./scripts/rename_project.sh --help` documents `--mode <enterprise|lean|plugin>`.
- Passing `--dry-run --mode lean` outputs the mode plan correctly.
- Passing invalid `--mode` exits 1 with a descriptive error message.

## Dependencies & Blockers
- Blocked by [Task 1](task_1_configure_mode_script.md).

## References & Rollback
- Reference: Spec Section 5.3.
- Rollback: `git checkout HEAD -- scripts/rename_project.sh`.
