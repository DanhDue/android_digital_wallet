---
id: "task_4_settings_presentation_mvi_viewmodel"
status: "todo"
priority: "high"
assignee: null
epic: "settings_language_darkmode"
dueDate: null
created: "2026-09-06T02:37:10+07:00"
modified: "2026-09-06T02:37:10+07:00"
completedAt: null
labels: ["architecture", "feature"]
order: "a4"
---

# Task 4: Settings Presentation MVI ViewModel

Epic: [settings_language_darkmode](../epic/settings_language_darkmode/settings_language_darkmode.en.md)

## Requirement Analysis
The presentation layer must adhere to the MVI pattern inherited from `MviViewModel<SettingsState, SettingsAction, SettingsEvent>` (`:packages:framework`):
1. `SettingsState`:
   - `isDarkMode: Boolean = false`
   - `selectedLanguageCode: String = "en"`
   - `selectedLanguageName: String = "English"`
   - `availableLanguages: List<SupportedLanguage> = emptyList()`
   - `isLanguagePickerVisible: Boolean = false`
   - `isLoadingLanguage: Boolean = false`
   - `error: UiText? = null`
2. `SettingsAction`:
   - `Init`: Dispatches silent bootstrap sync and observes theme/locale updates.
   - `ToggleDarkMode(val isDark: Boolean)`: Toggles dark mode.
   - `OpenLanguagePicker`: Opens the modal bottom sheet.
   - `DismissLanguagePicker`: Closes the modal bottom sheet.
   - `SelectLanguage(val languageCode: String)`: Triggers language change via `ChangeLanguageUseCase`.
   - `OpenProfile`, `OpenSecurity`, `OpenDeveloperOptions`, `Logout`: Feature navigation triggers.
3. `SettingsEvent`:
   - `NavigateToProfile`, `NavigateToSecurity`, `NavigateToDeveloperOptions`, `ShowToast(val message: UiText)`.
4. `SettingsViewModel`:
   - Injects `BootstrapSettingsUseCase`, `ChangeLanguageUseCase`, `ToggleDarkModeUseCase`, `AppThemeManager`, `AppLocalizationManager`, and `AppEventBus`.
   - Cancels any running language sync jobs if the user rapidly selects another language to avoid race conditions.

## Relevant Files & Context Pointers
- `features/settings/src/main/kotlin/com/danhdue/settings/presentation/SettingsState.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/presentation/SettingsAction.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/presentation/SettingsEvent.kt`
- `features/settings/src/main/kotlin/com/danhdue/settings/presentation/SettingsViewModel.kt`
- `features/settings/src/test/kotlin/com/danhdue/settings/presentation/SettingsViewModelTest.kt`

## Design Rationale
MVI guarantees predictable unidirectional data flow.
The ViewModel does not touch UI elements and exposes state solely through immutable `StateFlow<SettingsState>`.
Cancellation of in-flight language loading prevents out-of-order state overwrites.

## TDD Checklist
- [ ] **RED**: Write unit tests for:
  - `SettingsViewModel` on `Init`: runs silent bootstrap and populates `availableLanguages` without setting `isLoadingLanguage = true`.
  - `ToggleDarkMode`: updates `isDarkMode` state and executes use case.
  - `SelectLanguage`: handles loading state, switches language immediately if cached, shows and dismisses dialog appropriately.
  - Rapid language selection (race condition): ensures only the latest selection completes.
- [ ] **GREEN**: Implement minimal ViewModel and MVI Contract.
- [ ] **REFACTOR**: Ensure clean separation between Actions and Events, no hardcoded strings.

## Definition of Done (DoD)
- 100% tests pass on `SettingsViewModelTest`.
- Immutability of `SettingsState` guaranteed.
- Single responsibility for all action handlers.

## Dependencies & Blockers
- Blocked by [Task 3](task_3_settings_domain_orchestration_usecases.md)

## References & Rollback
- Epic HLD: [settings_language_darkmode.en.md](../epic/settings_language_darkmode/settings_language_darkmode.en.md)
- Rollback: Revert presentation files in `features/settings/src/main/kotlin/com/danhdue/settings/presentation/`.
