---
id: "task_7_e2e_verification_and_docs"
status: "todo"
priority: "high"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T15:20:00Z"
completedAt: null
labels: ["qa", "verification", "documentation", "e2e"]
order: "a7"
---

# Task 7: Execute End-to-End Verification Matrix & Update Architecture Documentation

Epic: [tri_mode_and_flutter_plugin_devbed](../epic/tri_mode_and_flutter_plugin_devbed/tri_mode_and_flutter_plugin_devbed.en.md)

## Requirement Analysis
Perform end-to-end acceptance validation across the 9 verification scenarios defined in the Design Spec, and update project architecture documentation:
1. **Execute 9-Point Verification Matrix:**
   - Scenario 1: `enterprise` mode build (`:konsist-test:test detekt spotlessCheck assembleDebug`).
   - Scenario 2: `lean` mode transition & build (`assembleDebug` succeeds, fast build).
   - Scenario 3: `plugin` mode transition (only `:plugin` and `:sample` active).
   - Scenario 4: `:plugin` independent compile & unit test (`:plugin:test :plugin:assembleRelease`).
   - Scenario 5: Background Worker test without FlutterEngine (`:plugin:testDebugUnitTest --tests "*Worker*"`).
   - Scenario 6: Standalone Testbed `:sample` build (`:sample:assembleDebug`).
   - Scenario 7: Round-trip idempotence (`enterprise` -> `lean` -> `plugin` -> `enterprise`).
   - Scenario 8: Project rename integration with `--mode` flag (`rename_project.sh --dry-run`).
   - Scenario 9: Mason Bricks generation test (`native_plugin`, `add_native_ui`).
2. **Update Core Documentation:**
   - Update `ARCHITECTURE.md` to document the 3 modes and the `:plugin` + `:sample` architecture.
   - Update `README.md` with usage instructions for `configure_mode.sh` and `--mode`.
   - Update `scripts/acceptance_check.sh` if needed to be mode-aware.

## Relevant Files & Context Pointers
- `README.md`
- `ARCHITECTURE.md`
- `docs/architecture/ARCHITECTURE.en.md`
- `docs/architecture/ARCHITECTURE.vi.md`
- `scripts/acceptance_check.sh`

## Design Rationale
- **Verification Before Completion:** Explicit evidence-based verification guarantees that all 3 modes operate reliably without residual compilation or formatting regressions.
- **Up-to-date Knowledge Base:** Documentation updates ensure onboarding developers immediately understand how to select their project profile.

## TDD Checklist
*TDD Adaptation:* End-to-end integration and verification gate. Verified by sequentially running the matrix commands and asserting all tests and build tasks exit with code 0.

- [ ] **RED**: Run automated verification checks on unconfigured state, capturing baseline.
- [ ] **GREEN**: Execute all 9 verification scenarios sequentially, verifying exit code 0 for each.
- [ ] **REFACTOR**: Update documentation (`ARCHITECTURE.md`, `README.md`), format code via `./gradlew spotlessApply`.

## Definition of Done (DoD)
- All 9 verification scenarios pass cleanly.
- `ARCHITECTURE.md` and `README.md` accurately reflect Tri-Mode usage.
- `./gradlew detekt spotlessCheck` passes across the repository.

## Dependencies & Blockers
- Blocked by [Task 1](task_1_configure_mode_script.md), [Task 2](task_2_rename_project_mode_integration.md), [Task 3](task_3_scaffold_plugin_module_pure_dagger.md), [Task 4](task_4_flutter_platform_and_compose_view.md), [Task 5](task_5_scaffold_sample_runner_app.md), and [Task 6](task_6_native_plugin_mason_bricks.md).

## References & Rollback
- Reference: Spec Section 7.
- Rollback: `git checkout HEAD -- README.md ARCHITECTURE.md docs/architecture/`.
