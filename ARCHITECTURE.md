# Architecture

The authoritative architecture guide has moved to
**[`docs/architecture/ARCHITECTURE.md`](docs/architecture/ARCHITECTURE.md)** (Android edition).

It covers, in the same section order as the cross-platform source
(`bloc_digital_wallet/.worktrees/flutter_super_app_template/docs/architecture/ARCHITECTURE.md`):

- **I. Clean Architecture + MVI** — core concepts, data-flow diagram, layer dependency rules
  (`Presentation → Domain ← Data`, pure-Kotlin domain).
- **II. MVI Mechanism** — `Action` / `State` / `Event`, the `MviViewModel` base, unidirectional
  data flow.
- **III. Feature-First Organization & Architecture Layers** — a feature module's directory
  structure, layer details, the **module map & dependency graph**, cross-feature communication
  rules (`AppRoutes` + `AppEventBus` + Hilt `@IntoSet EntryProviderInstaller`), and Mason usage.
- **IV. Modern Android Stack.**
- **V. Code Examples** — real `*Action` / `*State` / `*Event` / `*ViewModel : MviViewModel` /
  `*Screen @Composable` from `features/settings` and `features/home`.
- **VI. References** · **VII. Summary.**

## Module set (post god-module split — epic `android_super_app_template`, design §4.1)

| Module | Package | Role |
|---|---|---|
| `:core` | `com.danhdue.core` | Dependency floor: `DataState` / `NetworkResponse`, `DispatcherProvider`, `extension/*`, `pref/*`, `room/*`, `SessionManager`, `usecase/*`, `Logger`, `AppInitializer`. Compose-free, no project dependencies. |
| `:framework` | `com.danhdue.framework` | `MviViewModel` / `MvvmViewModel` / `BaseViewState` + the navigation3 host mechanism (`Navigator`, `NestedNavigator`, `ObserveBackstackForFlipper`). |
| `:network` | `com.danhdue.network` | Retrofit / OkHttp / Moshi wiring, interceptors, `apiCall` / `Failure`, `HttpStatusCode`, Flipper network tooling, token authenticator. |
| `:ui_kit` | `com.danhdue.uikit` | Shared Compose design system + runtime-permission handlers. |
| `:platform` | `com.danhdue.platform` | Cross-feature seam: `AppRoutes`, `AppEventBus`, `EntryProviderInstaller`, `FeatureEntry` / `FeatureInstaller`. |
| `:app` | `com.danhdue.androiddigitalwallet` | Thin composition root: Hilt aggregation, `NavDisplay`, `Application`, entry `Activity`. |
| `:features:*` | `com.danhdue.{feature}` | One product feature (data / domain / presentation). Depends only on the infrastructure modules — never on another feature. |
| `:libraries:testutils` | `com.danhdue.libraries.testutils` | Shared test rules and base test classes. |
| `:konsist-test` | `com.danhdue.konsist` | JVM/JUnit architecture gate (rules K1–K9). Never shipped in the APK. |

`libraries/` now holds only `testutils`; the former `libraries/framework`, `libraries/components`
and `libraries/jetframework` modules are gone.
