---
id: "task_5_mason_brick_sample_scaffolding"
status: "done"
priority: "high"
assignee: null
epic: "sandbox_and_contract_governance"
dueDate: null
created: "2026-09-10T23:53:00+07:00"
modified: "2026-09-11T00:37:00+07:00"
completedAt: "2026-09-11T00:37:00+07:00"
labels: ["mason", "scaffolding", "automation", "brick"]
order: "a5"
---

# Task 5: Mason Brick Automation for Sample Modules

Epic: [sandbox_and_contract_governance](../epic/sandbox_and_contract_governance/sandbox_and_contract_governance.en.md)

## Requirement Analysis
Upgrade the Mason code generator brick `bricks/mvi_feature` so that any future Mini App automatically includes its companion Sandbox runner out-of-the-box:
- Update `bricks/mvi_feature/__brick__/`:
  - Add `features/{{feature_name.snakeCase()}}/sample/build.gradle.kts` applying `commons.android-sample` and depending on the parent feature.
  - Add `features/{{feature_name.snakeCase()}}/sample/src/main/AndroidManifest.xml` with launcher Activity.
  - Add `features/{{feature_name.snakeCase()}}/sample/src/main/res/values/strings.xml` with app label.
  - Add `features/{{feature_name.snakeCase()}}/sample/src/main/kotlin/com/danhdue/features/{{feature_name.snakeCase()}}/sample/{{feature_name.pascalCase()}}SampleApp.kt`.
  - Add `features/{{feature_name.snakeCase()}}/sample/src/main/kotlin/com/danhdue/features/{{feature_name.snakeCase()}}/sample/{{feature_name.pascalCase()}}SampleActivity.kt`.
  - Add `features/{{feature_name.snakeCase()}}/sample/src/main/kotlin/com/danhdue/features/{{feature_name.snakeCase()}}/sample/di/{{feature_name.pascalCase()}}SampleModule.kt`.
- Update `bricks/mvi_feature/hooks/post_gen.dart`:
  - Automatically append `include(":features:{{feature_name.snakeCase()}}:sample")` to `settings.gradle.kts`.
- Test scaffolding a temporary test feature with `mason make mvi_feature --name dummy` and verify `:features:dummy:sample` is valid.

## Relevant Files & Context Pointers
- `bricks/mvi_feature/brick.yaml` (Mason metadata)
- `bricks/mvi_feature/__brick__/` (Scaffolding templates)
- `bricks/mvi_feature/hooks/post_gen.dart` (Post-generation hook for `settings.gradle.kts`)
- `scripts/remove_feature.sh` (Feature removal utility script if applicable)

## Design Rationale
Embedding the sample sandbox into the primary feature creation brick enforces the "Sandbox by Default" principle across all teams. Developers will never need to manually write sample harness boilerplate when starting a new Mini App.

## TDD Checklist
- [x] **RED**:
  - Check `bricks/mvi_feature/__brick__/` and observe that no `sample/` folder exists.
- [x] **GREEN**:
  - Add the `sample/` template files to `bricks/mvi_feature/__brick__/`.
  - Update `hooks/post_gen.dart` to insert `include(":features:{{feature_name.snakeCase()}}:sample")`.
  - Test code generation using Mason in a temporary directory or scratch environment; verify generated files compile.
- [x] **REFACTOR**:
  - Ensure template imports and package names use Mustache helpers (`snakeCase`, `pascalCase`) consistently.
  - Clean up any temporary test artifacts.
