# MVI Feature Brick

Generates a complete Android feature module with Clean Architecture layers (data, domain, presentation) following MVI pattern.

## Usage

```bash
# Run from project root - everything is automatic!
mason make mvi_feature \
  --name Payment \
  --package com.danhdue.payment \
  --screen Home
```

This automatically:
1. Creates `features/payment/` with all source files
2. Adds `include(":features:payment")` to `settings.gradle.kts`
3. Adds `featurePayment` to `Deps.kt` Modules object
4. Adds `FEATURE_PAYMENT` accessor to `DependencyHandlerExtensions.kt`
5. Adds import and `FEATURE_PAYMENT` to `app/build.gradle.kts`
6. Runs Gradle sync

## Variables

| Variable | Description | Default | Example |
|----------|-------------|---------|---------| 
| `name` | Feature name (PascalCase) | - | `Payment` |
| `package` | Base package path | - | `com.danhdue.payment` |
| `screen` | Initial screen name (PascalCase) | `Main` | `Home` |

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
