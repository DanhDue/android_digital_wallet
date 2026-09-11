---
id: "task_2_rename_project_mode_integration"
status: "todo"
priority: "medium"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T15:20:00Z"
completedAt: null
labels: ["tooling", "scripts", "automation"]
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

## TDD Checklist
*TDD Adaptation:* Script integration task. Verified by testing flag parsing, dry-run output assertions, and executing in a temporary git worktree.

- [ ] **RED**: Add test case asserting `scripts/rename_project.sh --dry-run` displays `--mode` selection in the rename plan, and fails if an invalid mode is provided.
- [ ] **GREEN**: Modify `scripts/rename_project.sh` to parse `--mode`, validate against `enterprise|lean|plugin`, print mode in plan, and delegate to `scripts/configure_mode.sh`.
- [ ] **REFACTOR**: Update usage documentation in `rename_project.sh` header and help text.

## Definition of Done (DoD)
- `./scripts/rename_project.sh --help` documents `--mode <enterprise|lean|plugin>`.
- Passing invalid mode (e.g. `--mode invalid`) exits 1 with a descriptive error message.
- Passing `--dry-run --mode lean` prints the mode plan without altering files.

## Dependencies & Blockers
- Blocked by [Task 1](task_1_configure_mode_script.md).

## References & Rollback
- Reference: Spec Section 5.3.
- Rollback: `git checkout HEAD -- scripts/rename_project.sh`.
