---
id: "task_4_acceptance_check_phase_6_api_check"
status: "todo"
priority: "high"
assignee: null
epic: "sandbox_and_contract_governance"
dueDate: null
created: "2026-09-10T23:53:00+07:00"
modified: "2026-09-10T23:53:00+07:00"
completedAt: null
labels: ["ci", "automation", "acceptance", "bcv"]
order: "a4"
---

# Task 4: CI & Acceptance Script Automation (Phase 6 apiCheck)

Epic: [sandbox_and_contract_governance](../epic/sandbox_and_contract_governance/sandbox_and_contract_governance.en.md)

## Requirement Analysis
Automate ABI contract verification in CI pipelines and pre-merge acceptance checks:
- Update `scripts/acceptance_check.sh`:
  - Add **Phase 6: Binary Compatibility & Contract Validation (`apiCheck`)**.
  - Execute `./gradlew apiCheck --stacktrace`.
  - Update total phase count in the console summary to 6.
- Ensure `./gradlew check` invokes `apiCheck` across all verified modules so standard GitHub Actions workflows enforce the gate automatically without custom scripts.
- Validate that the acceptance script executes cleanly with 0 warnings or errors.

## Relevant Files & Context Pointers
- `scripts/acceptance_check.sh` (Pre-merge acceptance verification script)
- `build.gradle.kts` (Ensure `apiCheck` runs as part of root `check` task)

## Design Rationale
Automating `apiCheck` as an explicit phase in `scripts/acceptance_check.sh` guarantees that no pull request can be merged if public ABI signatures diverge from the committed `.api` snapshots. It provides developers with immediate feedback if they accidentally introduce a breaking change.

## TDD Checklist
- [ ] **RED**:
  - Run `./scripts/acceptance_check.sh` and observe that only Phases 1 to 5 are executed.
- [ ] **GREEN**:
  - Add Phase 6 block executing `./gradlew apiCheck --stacktrace` in `scripts/acceptance_check.sh`.
  - Update success output message to state "All 6 Phases Passed Cleanly".
  - Run `./scripts/acceptance_check.sh` and verify all 6 phases pass successfully.
- [ ] **REFACTOR**:
  - Verify that if `packages/core/api/core.api` has an altered line, `scripts/acceptance_check.sh` immediately fails at Phase 6 with an exit code != 0.
  - Revert the test alteration and verify the script returns to a clean exit code 0.
