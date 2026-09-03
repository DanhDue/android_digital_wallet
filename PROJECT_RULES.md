# Android Super App Template — Project Rules (System Instructions)

You are an expert Android Developer. When assisting with this project, strictly adhere to the
following architectural and coding standards. The authoritative reference is
**[`docs/architecture/ARCHITECTURE.md`](docs/architecture/ARCHITECTURE.md)**; this file is the
short form.

## 🏗 Architecture & Design (Feature-First Clean Architecture)
- **Dependency Rule**: `Presentation` -> `Domain` <- `Data`.
  - Presentation MUST NOT call Data directly.
  - Domain MUST NOT import anything from Presentation or Data.
  - Domain layer MUST be **Pure Kotlin** (No Android Framework dependencies like Context, View, etc.).
- **Unidirectional Data Flow (UDF)**: `View` ➡️ `ViewModel` ➡️ `Domain` ➡️ `Data` ➡️ `Domain` ➡️ `ViewModel` ➡️ `View`.

## 🎨 Presentation Layer (MVI Mechanism)
- **State (ViewModel ➡️ View)**: Persistent UI state. Use `MutableStateFlow` in ViewModel and `collectAsStateWithLifecycle()` in View.
- **Action (View ➡️ ViewModel)**: User inputs/triggers (Click, Text Change). ViewModel should have a single `onAction(action: Action)` entry point.
- **Event (ViewModel ➡️ View)**: One-shot side effects (Navigation, Toasts, Dialogs). Use `Channel` and `receiveAsFlow()`.
- **Stateless UI**: Keep Compose screens "dumb." They should only receive State and emit Actions.

## 🟡 Domain Layer (Business Logic)
- **UseCases**: Every business logic operation must be encapsulated in a single UseCase class (Single Responsibility).
- **Entities**: Pure Kotlin data classes. No library-specific annotations (Gson, Room, etc.).
- **Repository Interfaces**: Defined in the Domain layer to maintain abstraction.

## 🔵 Data Layer (Infrastructure)
- **Repository Implementation**: Implements the Domain's interface. Coordinates between Remote (API) and Local (DB) data sources.
- **DTOs**: Data Transfer Objects for API/DB models. Contains mapping logic or use separate Mappers to convert `DTO` ↔️ `Entity`.
- **Network Results**: Use `NetworkResult<T>` (Success/Error) for all asynchronous data flows to avoid `kotlin.Result` KSP/serialization issues in multi-module environments.

## ⚙️ Build System & Project Structure
- **buildSrc**: Centralized management for all dependencies and versions.
  - **Versions**: `Versions.kt`
  - **Dependencies**: `Deps.kt`
  - **Modules**: `Modules` object in `Deps.kt`.
- **No Hardcoding**: Never hardcode version strings or library paths in `build.gradle.kts` files.
- **Multi-module**: Respect module boundaries. Features live in `:features:*` and depend **only**
  on the infrastructure modules `:core` / `:framework` / `:network` / `:ui_kit` / `:platform` —
  never on another feature. `:core` is the floor (no project dependencies); `:framework` is only
  for modules with UI/state. `:core` + `:platform` are wired automatically by the
  `commons.android-feature` convention. Cross-feature traffic goes through `:platform`
  (`AppRoutes` / `AppEventBus` / `@IntoSet EntryProviderInstaller`). Konsist (K1–K9) + a Gradle
  guard enforce this — run `./gradlew :konsist-test:test`.

## 📝 Naming Conventions
- **Contract Classes**: `[Feature]State`, `[Feature]Action`, `[Feature]Event`.
- **ViewModel**: `[Feature]ViewModel`.
- **UseCase**: `[Action][Feature]UseCase` (e.g., `GetProfileDataUseCase`).
- **Screen**: `[Feature]Screen` and `[Feature]Root` (ViewModel entry point).
