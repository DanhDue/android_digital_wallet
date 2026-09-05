---
id: "task_3_settings_domain_orchestration_usecases"
status: "done"
priority: "high"
assignee: null
epic: "settings_language_darkmode"
dueDate: null
created: "2026-09-06T02:37:10+07:00"
modified: "2026-09-06T02:49:15+07:00"
completedAt: "2026-09-06T02:49:15+07:00"
labels: ["architecture", "feature"]
order: "a3"
---

# Task 3: Settings Domain Orchestration Use Cases

Epic: [settings_language_darkmode](../epic/settings_language_darkmode/settings_language_darkmode.en.md)

## Requirement Analysis
The domain layer must encapsulate all business logic in pure Kotlin, free of any Android framework dependencies (enforced by Konsist K3 rule):
1. Domain Entities:
   - `SupportedLanguage`: `code: String`, `name: String`, `version: String`, `isDefault: Boolean`, `isActive: Boolean`, `isCached: Boolean`.
   - `LanguageSyncStatus`: Sealed hierarchy (`Idle`, `Loading`, `CachedApplied`, `Success`, `Error`).
2. Domain Repository Contract:
   - `SettingsRepository`: Interface declaring `bootstrap()`, `fetchAndCacheTranslations(code)`, `getCachedLanguages()`, `getActiveLanguage()`.
3. Use Cases:
   - `BootstrapSettingsUseCase`: Calls `SettingsRepository.bootstrap()` silently in background to update available languages and cache metadata.
   - `ChangeLanguageUseCase`: Flow-based use case. If target language is bundled (`en`, `vi`) or locally cached, immediately applies it (optimistic switch) and triggers a background check for delta updates; if uncached, emits `Loading`, fetches translations from remote, caches them, applies dynamic translations via `AppLocalizationManager`, and emits `Success` (or `Error` on failure).
   - `ToggleDarkModeUseCase`: Updates `AppThemeManager` with the requested theme mode (`DARK` vs `LIGHT`).

## Relevant Files & Context Pointers
- `features/settings/src/main/kotlin/com/danhdue/settings/domain/model/SupportedLanguage.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/domain/model/LanguageSyncStatus.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/domain/repository/SettingsRepository.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/domain/usecase/BootstrapSettingsUseCase.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/domain/usecase/ChangeLanguageUseCase.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/domain/usecase/ToggleDarkModeUseCase.kt`
- `features/settings/src/test/kotlin/com/danhdue/settings/domain/usecase/BootstrapSettingsUseCaseTest.kt`
- `features/settings/src/test/kotlin/com/danhdue/settings/domain/usecase/ChangeLanguageUseCaseTest.kt`
- `features/settings/src/test/kotlin/com/danhdue/settings/domain/usecase/ToggleDarkModeUseCaseTest.kt`

## Design Rationale
Clean Architecture dictates pure Kotlin domain layer.
UseCases should do one thing and follow Single Responsibility Principle.
`ChangeLanguageUseCase` returns a `Flow<LanguageSyncStatus>` so the UI can reactively transition from optimistic applied state to background sync completion or display loading dialog.

## TDD Checklist
- [x] **RED**: Write unit tests for:
  - `BootstrapSettingsUseCase`: Returns updated supported languages list on success, returns cached list on failure.
  - `ChangeLanguageUseCase`: Emits `CachedApplied` immediately for cached/bundled language; emits `Loading` -> `Success` for uncached language; emits `Error` on network failure.
  - `ToggleDarkModeUseCase`: Verifies theme manager is called with correct theme mode.
- [x] **GREEN**: Implement minimal code:
  - Create domain models and interface.
  - Implement the three use cases.
- [x] **REFACTOR**: Verify pure Kotlin imports (no `android.*` or `androidx.*`).

## Definition of Done (DoD)
- 100% test pass on use cases.
- Strict compliance with Konsist K3 rule.
- Full branch coverage of optimistic vs loading paths in `ChangeLanguageUseCase`.

## Dependencies & Blockers
- Blocked by [Task 2](task_2_settings_data_layer_api_integration.md)

## References & Rollback
- Epic HLD: [settings_language_darkmode.en.md](../epic/settings_language_darkmode/settings_language_darkmode.en.md)
- Rollback: Revert domain changes in `features/settings/src/main/kotlin/com/danhdue/settings/domain/`.
