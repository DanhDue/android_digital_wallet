# MVI Feature Brick

Generates a complete Android feature module with Clean Architecture layers (data, domain, presentation) following MVI pattern.

## Usage

```bash
# Run from project root - everything is automatic!
mason make mvi_feature \
  --name Payment \
  --package com.danhdue.payment \
  --screen Home \
  --delivery install-time
```

### `--delivery install-time` (default)
1. Creates `features/payment/` with all source files
2. Adds `include(":features:payment")` to `settings.gradle.kts`
3. Adds `featurePayment` to `Deps.kt` Modules object
4. Adds `FEATURE_PAYMENT` accessor to `DependencyHandlerExtensions.kt`
5. Adds import and `FEATURE_PAYMENT` to `app/build.gradle.kts` (and `shell/build.gradle.kts` once `:shell` exists — guarded)
6. Appends `PaymentRoute` to `:platform` `AppRoutes.kt`
7. Registers entry point in `:platform` `AppDeepLinks.kt`
8. Runs Gradle sync

The feature exposes its navigation entries and deep link resolution through the generated
`PaymentNavigationModule` (`@Module` in `SingletonComponent` → `@Provides @IntoSet EntryProviderInstaller` and `@Provides @IntoSet DeepLinkResolver`).

### `--delivery on-demand` (Dynamic Feature Module)
On top of step 1–2 above:
* rewrites `features/payment/build.gradle.kts` to apply `com.android.dynamic-feature` (inverted dep: `implementation(project(":app"))`)
* adds `":features:payment"` to `:app` `android.dynamicFeatures`
* rewrites the manifest with `<dist:module dist:onDemand="true">` + a title string resource
* generates `PaymentFeatureEntry : com.danhdue.platform.FeatureEntry` (providing installer and `PaymentDeepLinkResolver`) + `resources/META-INF/services/com.danhdue.platform.FeatureEntry`
* appends `@Serializable data object PaymentRoute : NavKey` to `:platform` `AppRoutes`
* registers entry point with `dynamicModule = "payment"` in `:platform` `AppDeepLinks`
* registers the feature in `:shell` `OnDemandFeatures`

### Deep Links and Tab Placement
Every feature automatically resolves its base deep link:
- `myapp://<feature>` -> `AppRoutes.<Feature>Route`
- By default, `AppDeepLinks.kt` registers `tab = null` (which resolves to `RootFullScreen` placement).
- **TODO**: If the feature is hosted inside a Shell bottom navigation tab, update `tab = <tabIndex>` in `AppDeepLinks.kt` (e.g., `tab = 0`).

> The `SplitInstallManager` runtime + `:shell` install branch are **Task 14**. The Hilt Gradle plugin does not support dynamic-feature modules, so the generated DFM keeps `hilt-android` on the compile classpath only (no processing) — its DI wiring is Task 14's job. Until then the split is bundled by `assembleDebug` and behaves like install-time.

## Variables

| Variable | Description | Default | Example |
|----------|-------------|---------|---------| 
| `name` | Feature name (PascalCase) | - | `Payment` |
| `package` | Base package path | - | `com.danhdue.payment` |
| `screen` | Initial screen name (PascalCase) | `Main` | `Home` |
| `delivery` | `install-time` \| `on-demand` (unknown → `install-time` + warning) | `install-time` | `on-demand` |

## Generated Structure

```
features/payment/
├── build.gradle.kts
└── src/main/kotlin/
    ├── data/
    │   ├── di/PaymentDataModule.kt
    │   ├── mappers/PaymentMapper.kt
    │   ├── models/PaymentDto.kt
    │   └── repository/PaymentRepositoryImpl.kt
    ├── domain/
    │   ├── di/PaymentDomainModule.kt
    │   ├── entities/PaymentEntity.kt
    │   ├── repository/PaymentRepository.kt
    │   └── usecase/GetPaymentDataUseCase.kt
    └── presentation/
        ├── di/PaymentNavigationModule.kt
        └── home/   (camelCase for ktlint)
            ├── models/HomeUiModel.kt
            ├── HomeAction.kt, HomeEvent.kt, HomeState.kt
            ├── HomeViewModel.kt, HomeScreen.kt
```

## Adding More Screens

Use the `mvi_subfeature` brick:

```bash
mason make mvi_subfeature \
  --module payment \
  --name Detail \
  --package com.danhdue.payment
```
