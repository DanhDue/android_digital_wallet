# Skills Manifest

Index of all available AI Agent skills in the project.

| Skill                        | Trigger                     | How to use                                                                                        | Description                                                                                                                                  | File                                                           |
|------------------------------|-----------------------------|---------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------|
| **API Integration** | `@api_integration` | Use `@api_integration` for Authentication feature, base URL https://api.wallet.com/user/ | Generates a standardized Network Module, API Interface, and Remote Data Source for a specific feature using the Hybrid Network Architecture. | [SKILL.md](../.agent/skills/api_integration/SKILL.md) |
| **PR Review** | `@pr_review` | Use `@pr_review` to review commit: \<COMMIT_ID\> or Use `@pr_review` to review this PR: \<DIFF\> | Performs comprehensive Pull Request reviews focusing on Clean Architecture, Jetpack Compose, Security, and Fintech standards. Uses `@quality_check` skill first. | [SKILL.md](../.agent/skills/pr_review/SKILL.md) |
| **Moshi DTO Generator** | `@moshi_dto` | Use `@moshi_dto` to convert this JSON: \<JSON_BODY\> | Converts raw JSON structures into clean, production-ready Moshi DTOs with proper annotations and naming conventions. | [SKILL.md](../.agent/skills/moshi_dto_generator/SKILL.md) |
| **Quality Check** | `@quality_check` | Use `@quality_check` to fix all code quality issues | Runs `./gradlew check` to identify issues (ktlint, detekt, Spotless, tests) and automatically fixes all problems. Includes `cleanup-java` for resource management. | [SKILL.md](../.agent/skills/quality_check/SKILL.md) |

## Usage

To use a skill, simply mention the **Trigger** in your request (e.g., "Use `@api_integration` to create a payment network module").
