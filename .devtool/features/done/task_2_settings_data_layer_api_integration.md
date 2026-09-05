---
id: "task_2_settings_data_layer_api_integration"
status: "done"
priority: "high"
assignee: null
epic: "settings_language_darkmode"
dueDate: null
created: "2026-09-06T02:37:10+07:00"
modified: "2026-09-06T02:47:35+07:00"
completedAt: "2026-09-06T02:47:35+07:00"
labels: ["architecture", "feature"]
order: "a2"
---

# Task 2: Settings Remote APIs & Data Layer Integration

Epic: [settings_language_darkmode](../epic/settings_language_darkmode/settings_language_darkmode.en.md)

## Requirement Analysis
The Settings feature requires communication with two backend endpoints:
1. `POST /api/v1/settings/sync/bootstrap`:
   - Request body: `{"cached_translations": [{"resource_id": "en_US", "version": "0.0.1"}]}`.
   - Response: `{"available_languages": [...], "stale_translations": [...]}`.
2. `GET /api/v1/translations/{code}?since_version={version}`:
   - Response: Nested JSON tree of strings for the target locale.
3. Data Layer Components:
   - Moshi DTOs: `BootstrapRequestDto`, `BootstrapResponseDto`, `SupportedLanguageDto`, `StaleTranslationDto`.
   - Utility `JsonFlattener`: Converts nested map structure `{"settings": {"preferences": {"darkMode": "..."}}}` into flat dot-notation keys (`settings.preferences.darkMode = "..."`).
   - `SettingsLocalDataSource`: Handles caching of supported language list, translation dot-maps, and versions using `CacheStore` (`:packages:core`).
   - `DefaultSettingsRepository`: Integrates `SettingsApiService`, `SettingsLocalDataSource`, and `AppLocalizationManager`.

## Relevant Files & Context Pointers
- `features/settings/src/main/kotlin/com/danhdue/settings/data/remote/SettingsApiService.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/data/remote/dto/BootstrapRequestDto.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/data/remote/dto/BootstrapResponseDto.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/data/util/JsonFlattener.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/data/local/SettingsLocalDataSource.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/data/repository/DefaultSettingsRepository.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/di/SettingsDataModule.kt`
- `features/settings/src/test/kotlin/com/danhdue/settings/data/util/JsonFlattenerTest.kt`
- `features/settings/src/test/kotlin/com/danhdue/settings/data/local/SettingsLocalDataSourceTest.kt`
- `features/settings/src/test/kotlin/com/danhdue/settings/data/repository/DefaultSettingsRepositoryTest.kt`

## Design Rationale
Leverage the project's existing network stack (`:packages:network`) utilizing Retrofit and Moshi.
Applicable skills: `api_integration`, `moshi_dto_generator`.
DTOs must use `@JsonClass(generateAdapter = true)` with `@Json(name = "...")` to adhere to Moshi guidelines.

## TDD Checklist
- [x] **RED**: Write unit tests for:
  - `JsonFlattener`: Correctly flattens multi-level nested maps and preserves primitive string values.
  - `SettingsLocalDataSource`: Correctly serializes and deserializes language lists and flattened translation maps via `CacheStore`.
  - `DefaultSettingsRepository`: Correctly handles bootstrap call, maps DTOs to Domain models, and downloads & caches translations.
- [x] **GREEN**: Implement minimal data layer code:
  - Create DTOs with Moshi annotations.
  - Create `SettingsApiService`.
  - Implement `JsonFlattener` and `SettingsLocalDataSource`.
  - Implement `DefaultSettingsRepository`.
  - Wire Hilt dependencies in `SettingsDataModule`.
- [x] **REFACTOR**: Ensure no leaked DTOs into domain interfaces, use pure Kotlin mappers.

## Definition of Done (DoD)
- Unit tests pass with >85% coverage on data layer.
- Konsist architecture rules (K2, K3, K4) pass.
- Remote failure scenarios safely return domain `Failure` / `Result.failure` without unhandled exceptions.

## Dependencies & Blockers
- Blocked by [Task 1](task_1_platform_theme_localization_infrastructure.md)

## References & Rollback
- Epic HLD: [settings_language_darkmode.en.md](../epic/settings_language_darkmode/settings_language_darkmode.en.md)
- Rollback: Revert data layer changes in `features/settings/src/main/kotlin/com/danhdue/settings/data/`.
