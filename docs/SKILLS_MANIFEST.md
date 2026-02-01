# Skills Manifest

Index of all available AI Agent skills in the project.

| Skill                        | Trigger                     | How to use                                                                                        | Description                                                                                                                                  | File                                                           |
|------------------------------|-----------------------------|---------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------|
| **API Integration** | `@api_integration` | Use `@api_integration` for Authentication feature, base URL https://api.wallet.com/user/ | Generates a standardized Network Module, API Interface, and Remote Data Source for a specific feature using the Hybrid Network Architecture. | [SKILL.md](../.agent/skills/api_integration/SKILL.md) |
| **PR Review** | `@pr_review` | Use `@pr_review` to review commit: \<COMMIT_ID\> or Use `@pr_review` to review this PR: \<DIFF\> | Performs comprehensive Pull Request reviews focusing on Clean Architecture, Jetpack Compose, Security, and Fintech standards. Runs `./gradlew check` first. | [SKILL.md](../.agent/skills/pr_review/SKILL.md) |
| **Moshi DTO Generator** | `@moshi_dto` | Use `@moshi_dto` to convert this JSON: \<JSON_BODY\> | Converts raw JSON structures into clean, production-ready Moshi DTOs with proper annotations and naming conventions. | [SKILL.md](../.agent/skills/moshi_dto_generator/SKILL.md) |

## Usage

To use a skill, simply mention the **Trigger** in your request (e.g., "Use `@api_integration` to create a payment network module").
