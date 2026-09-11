---
id: "task_6_native_plugin_mason_bricks"
status: "todo"
priority: "medium"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T15:20:00Z"
completedAt: null
labels: ["mason", "scaffolding", "tooling", "bricks"]
order: "a6"
---

# Task 6: Implement Mason Bricks (native_plugin & add_native_ui)

Epic: [tri_mode_and_flutter_plugin_devbed](../epic/tri_mode_and_flutter_plugin_devbed/tri_mode_and_flutter_plugin_devbed.en.md)

## Requirement Analysis
Implement two new Mason Bricks in `bricks/` to standardize scaffolding of native plugin modules:
1. **`bricks/native_plugin`:**
   - Variables: `name` (string), `package` (string, e.g. `com.danhdue.biometric`), `has_ui` (boolean, default: false).
   - Generates an Android Library module structure following Clean Architecture with Pure Dagger 2.
   - When `has_ui == false`: Omits `presentation/` and generates Pigeon contract stub in `platform/`.
   - When `has_ui == true`: Includes `presentation/` (Base MviViewModel, Compose Screen, PlatformView) and enables Compose compiler in `build.gradle.kts`.
2. **`bricks/add_native_ui`:**
   - Variables: `name` (string), `package` (string).
   - Upgrades an existing headless plugin by generating `presentation/` and patching `build.gradle.kts` to enable Jetpack Compose.
3. Register both bricks in root `mason.yaml`.
4. Provide a validation script `scripts/test_mason_bricks.sh` to make bricks and verify compilation.

## Relevant Files & Context Pointers
- `mason.yaml`
- `bricks/native_plugin/brick.yaml` [NEW]
- `bricks/native_plugin/__brick__/` [NEW]
- `bricks/add_native_ui/brick.yaml` [NEW]
- `bricks/add_native_ui/__brick__/` [NEW]
- `scripts/test_mason_bricks.sh` [NEW]

## Design Rationale
- **Cross-Platform Brick Parity:** Matches the exact semantics and folder structures of `pac_native_plugin` and `pac_add_native_ui` in Flutter Super App Template (`bloc_digital_wallet`), enabling effortless bidirectional knowledge sharing.
- **Repeatable Scaffolding:** Eliminates manual boilerplate when creating additional native plugins.

## TDD Checklist
*TDD Adaptation:* Code generation scaffolding task. Verified using an automated test script that executes `mason make` with various flags into a temporary directory and runs `./gradlew compileReleaseKotlin` on the generated code.

- [ ] **RED**: Write `scripts/test_mason_bricks.sh` asserting failure when bricks are uninstalled or generate malformed syntax.
- [ ] **GREEN**: Implement `bricks/native_plugin` and `bricks/add_native_ui`, register in `mason.yaml`. Verify `test_mason_bricks.sh` succeeds.
- [ ] **REFACTOR**: Ensure template files use standard Kotlin formatting and proper variable interpolations (`{{name.pascalCase()}}`, `{{package}}`).

## Definition of Done (DoD)
- `mason make native_plugin --name custom_sensor --package com.danhdue.sensor --has_ui false` generates a compiling library.
- `mason make add_native_ui --name custom_sensor --package com.danhdue.sensor` successfully adds Compose UI.
- `scripts/test_mason_bricks.sh` passes 100%.

## Dependencies & Blockers
- Blocked by [Task 3](task_3_scaffold_plugin_module_pure_dagger.md) and [Task 4](task_4_flutter_platform_and_compose_view.md).

## References & Rollback
- Reference: Spec Section 6.
- Rollback: `git checkout HEAD -- mason.yaml && rm -rf bricks/native_plugin bricks/add_native_ui scripts/test_mason_bricks.sh`.
