---
name: Implement Network Module
description: Generates a standardized Network Module, API Interface, and Remote Data Source for a specific feature using the Hybrid Network Architecture.
---

# Implement Network Module Skill

> [!IMPORTANT]
> **CHECK**: Check if the generated code for ${FEATURE_NAME}RemoteDataSource correctly handles the ApiError case without using try-catch.

**Role**: Senior Android Architect Specialist.

**Objective**: Generate a standardized Network Module and Remote Data Source for a specific feature, integrating it with the existing `libraries:framework` (Hybrid Network Architecture).

## Input Parameters

*   `FEATURE_NAME`: (e.g., "Payment", "Voucher", "CardManagement")
*   `BASE_URL`: (The specific API endpoint for this feature)
*   `API_ENDPOINTS`: (List of methods, paths, and return types)

## Implementation Steps

### 1. Feature Network Module
Create `${FEATURE_NAME}NetworkModule.kt` in the feature's DI package (`com.danhdue.${feature_name_lowercase}.data.di`).

*   Inject `Retrofit.Builder` from `:libraries:framework`.
*   Configure a specialized `OkHttpClient` using `.newBuilder()` if specific interceptors (like Auth) are needed.
*   Provide a Singleton `Retrofit` instance (named specific to the feature) and the `${FEATURE_NAME}Api` service.

### 2. API Interface
Create `${FEATURE_NAME}Api.kt` in the feature's remote package (`com.danhdue.${feature_name_lowercase}.data.datasources.remote`).

*   Every function must return `NetworkResponse<T>` (from framework).
*   Every function must include a `@Tag` parameter with `FeatureConfig(appId = "...", featureName = "${FEATURE_NAME}")`.

### 3. Remote Data Source
Create `${FEATURE_NAME}RemoteDataSource.kt` in the feature's remote package (`com.danhdue.${feature_name_lowercase}.data.datasources.remote`).

*   Use Constructor Injection (`@Inject constructor`).
*   Implement methods that call the API and handle the `NetworkResponse` logic.

## Constraint Rules

*   **NO TRY-CATCH**: Rely entirely on the Custom CallAdapter from the framework.
*   **KOTLIN ONLY**: Use Clean Architecture patterns.
*   **HILT**: Use `@Module`, `@InstallIn(SingletonComponent::class)`, and `@Provides`.


## Output Format
Provide the complete file structure and source code for the generated files within the `:features:${FEATURE_NAME}` module.

## Example Usage

```markdown
Use the Implement Network Module Skill to create network components for:
- FEATURE_NAME: "Payment"
- BASE_URL: "https://api.wallet.com/payment/"
- API_ENDPOINTS:
  - POST /pay (body: PaymentRequest) -> PaymentResponse
  - GET /history -> List<PaymentHistory>
```
