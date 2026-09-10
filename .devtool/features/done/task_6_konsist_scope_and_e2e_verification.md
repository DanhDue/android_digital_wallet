---
id: "task_6_konsist_scope_and_e2e_verification"
status: "done"
priority: "high"
assignee: null
epic: "sandbox_and_contract_governance"
dueDate: null
created: "2026-09-10T23:53:00+07:00"
modified: "2026-09-11T00:58:00+07:00"
completedAt: "2026-09-11T00:58:00+07:00"
labels: ["konsist", "e2e", "acceptance", "quality"]
order: "a6"
---

# Task 6: Konsist Gate Rules & E2E Verification

Epic: [sandbox_and_contract_governance](../epic/sandbox_and_contract_governance/sandbox_and_contract_governance.en.md)

## Requirement Analysis
Ensure that the addition of `:sample` subprojects and BCV integration preserves 100% compliance with the repository's Konsist architecture gates (rules K1–K10):
- Review `konsist-test/src/test/kotlin/com/danhdue/konsist/support/ArchScope.kt`:
  - Ensure sample runners (`:features:*:sample`) are accurately scoped and classified as standalone testbeds without triggering false-positive violations in library layer checks.
  - Verify Rule K1: A sample module imports only its target feature (`:features:settings:sample` imports solely `com.danhdue.settings.*`), zero cross-feature imports.
  - Verify Rule K6: Each sample imports at most 1 feature, maintaining `importedFeatures.size <= 1`.
  - Verify Rule K8: Sample modules never import Host `:app` or `:shell`.
- Perform full end-to-end acceptance run across all verification tools:
  - `./gradlew :konsist-test:test`
  - `./gradlew apiCheck`
  - `./gradlew :features:settings:sample:assembleDebug`
  - `./gradlew assembleDebug`
  - `./gradlew detekt spotlessCheck`
  - `./scripts/acceptance_check.sh` (All 6 Phases passing)

## Relevant Files & Context Pointers
- `konsist-test/src/test/kotlin/com/danhdue/konsist/support/ArchScope.kt` (Konsist scope definitions)
- `konsist-test/src/test/kotlin/com/danhdue/konsist/BoundaryRulesTest.kt` (K1, K6 rules)
- `konsist-test/src/test/kotlin/com/danhdue/konsist/HostRulesTest.kt` (K8 rule)
- `scripts/acceptance_check.sh` (Full pipeline verification)

## Design Rationale
A Super App's governance framework is only as good as its enforcement mechanism. Verifying that the Konsist gate natively embraces the `:sample` runner architecture ensures that the repository remains strictly governed as new teams add dozens of Mini Apps.

## TDD Checklist
- [x] **RED**:
  - Run `./gradlew :konsist-test:test` and observe any scope classification discrepancies with sample runners.
- [x] **GREEN**:
  - Update `ArchScope.kt` if necessary to explicitly categorize sample modules as standalone sample runners.
  - Verify all 10 Konsist test suites (`BoundaryRulesTest`, `HostRulesTest`, `LayerRulesTest`, `NamingRulesTest`, etc.) pass with 0 errors.
  - Execute `./scripts/acceptance_check.sh` and verify all 6 phases succeed.
- [x] **REFACTOR**:
  - Verify git working tree is clean and `konsist_boundary_whitelist.txt` remains empty.
