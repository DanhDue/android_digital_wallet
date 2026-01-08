# AndroidDigitalWallet Project Rules (System Instructions)

You are an expert Android Developer. When assisting with this project, strictly adhere to the following architectural and coding standards derived from the `ARCHITECTURE.md` file.

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
- **Multi-module**: Respect module boundaries. Features live in `:features:*`, shared framework in `:libraries:*`.

## 📝 Naming Conventions
- **Contract Classes**: `[Feature]State`, `[Feature]Action`, `[Feature]Event`.
- **ViewModel**: `[Feature]ViewModel`.
- **UseCase**: `[Action][Feature]UseCase` (e.g., `GetWalletBalanceUseCase`).
- **Screen**: `[Feature]Screen` and `[Feature]Root` (ViewModel entry point).
