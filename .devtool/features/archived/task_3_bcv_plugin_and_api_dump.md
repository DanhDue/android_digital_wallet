---
id: "task_3_bcv_plugin_and_api_dump"
status: "done"
priority: "high"
assignee: null
epic: "sandbox_and_contract_governance"
dueDate: null
created: "2026-09-10T23:53:00+07:00"
modified: "2026-09-11T00:29:45+07:00"
completedAt: "2026-09-11T00:29:45+07:00"
labels: ["bcv", "contract", "abi", "governance"]
order: "a3"
---

# Task 3: Binary Compatibility Validator (BCV) Integration

Epic: [sandbox_and_contract_governance](../epic/sandbox_and_contract_governance/sandbox_and_contract_governance.en.md)

## Requirement Analysis
Integrate JetBrains' **Binary Compatibility Validator (BCV)** to track and protect the public ABI surface of the Super App's shared packages:
- Declare plugin dependency `org.jetbrains.kotlinx.binary-compatibility-validator:0.17.0` in `buildSrc`.
- Apply BCV in the root `build.gradle.kts` and configure `apiValidation`:
  - Enforce validation strictly on the 5 foundation packages:
    - `:packages:core`
    - `:packages:platform`
    - `:packages:network`
    - `:packages:framework`
    - `:packages:ui_kit`
  - Ignore internal host apps, shells, test modules, and feature modules:
    `ignoredProjects += listOf("app", "shell", "konsist-test", "libraries:testutils") + subprojects.filter { it.path.startsWith(":features") }.map { it.name }`
  - Ignore internal packages and non-public markers (`*.internal`, `@InternalApi`).
- Execute `./gradlew apiDump` to create the initial verified baseline `.api` dump files:
  - `packages/core/api/core.api`
  - `packages/platform/api/platform.api`
  - `packages/network/api/network.api`
  - `packages/framework/api/framework.api`
  - `packages/ui_kit/api/ui_kit.api`
- Execute `./gradlew apiCheck` to verify clean pass on the generated baseline.

## Relevant Files & Context Pointers
- `buildSrc/build.gradle.kts` (Add BCV plugin to classpath)
- `build.gradle.kts` (Root build script applying and configuring `apiValidation`)
- `packages/core/api/core.api` (Generated contract dump)
- `packages/platform/api/platform.api` (Generated contract dump)
- `packages/network/api/network.api` (Generated contract dump)
- `packages/framework/api/framework.api` (Generated contract dump)
- `packages/ui_kit/api/ui_kit.api` (Generated contract dump)

## Design Rationale
In a modular Super App, public signatures in shared packages are treated as immutable protocol contracts. BCV ensures that any unauthorized or accidental change to a public function, method parameter, or data class breaks compilation during PR review, rather than crashing in production.

## TDD Checklist
- [x] **RED**:
  - Run `./gradlew apiCheck` before configuring the plugin and generating `.api` files; verify task does not exist or fails.
- [x] **GREEN**:
  - Add BCV plugin to `buildSrc` dependencies and apply in root `build.gradle.kts`.
  - Configure `apiValidation` scope to target only the 5 shared package modules.
  - Run `./gradlew apiDump` to generate the 5 `.api` signature files.
  - Run `./gradlew apiCheck` and verify `BUILD SUCCESSFUL`.
- [x] **REFACTOR**:
  - Verify that adding a temporary dummy method to `packages/core/src/main/kotlin/com/danhdue/core/extensions/StringExtensions.kt` causes `./gradlew apiCheck` to immediately fail with a signature mismatch.
  - Revert the dummy change and confirm `./gradlew apiCheck` returns to passing clean.
