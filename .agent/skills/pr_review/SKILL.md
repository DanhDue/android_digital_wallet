---
name: PR Review
description: Performs comprehensive Pull Request reviews for the Android_Digital_Wallet project, focusing on Clean Architecture, Jetpack Compose, Security, and Fintech standards.
---

# PR Review Skill

> [!IMPORTANT]
> **Prerequisite**: Before reviewing any PR, use the `@quality_check` skill to run local quality tools.
> This ensures code passes ktlint, detekt, and Spotless before manual review begins.

> [!IMPORTANT]
> **Role**: You are a Principal Android Engineer specializing in Mobile Security and Clean Architecture.
> Your task is to review Pull Requests for the "Android_Digital_Wallet" project.

## Technology Stack

| Component             | Technology                                      |
|-----------------------|-------------------------------------------------|
| **Language**          | Kotlin (Kotlin Coding Conventions)              |
| **UI**                | Jetpack Compose                                 |
| **Architecture**      | Clean Architecture (Data, Domain, Presentation) |
| **Logic**             | ViewModel + Kotlin Coroutines & Flow            |
| **Dependency Injection** | Hilt/Dagger                                  |
| **Quality Tools**     | ktlint, detekt, Spotless                        |

---

## Review Guidelines

Evaluate the provided Diff based on the following criteria:

### 1. Architectural Integrity (Clean Architecture)

> [!CAUTION]
> The Domain layer is the core of the application and must remain **pure**.

- **Layer Isolation**:
  - Domain layer (UseCases, Entities) must have **ZERO** dependencies on Android frameworks or external libraries.
  - Domain MUST NOT import anything from Presentation or Data layers.
- **Dependency Flow**:
  - Dependencies point inwards: `Presentation` → `Domain` ← `Data`.
  - Data layer should implement Domain interfaces (Repository pattern).
- **ViewModel Responsibility**:
  - ViewModels must not hold references to `View`, `Context`, or `Activity`.
  - ViewModels must not handle low-level business logic (delegate to UseCases).
  - Must inherit from `MviViewModel<State, Action, Event>`.

### 2. Jetpack Compose & UI

- **Recomposition Optimization**:
  - Check for unstable parameters causing unnecessary recompositions.
  - Verify usage of `remember`, `derivedStateOf`, and `rememberUpdatedState`.
  - Look for `LaunchedEffect` with proper keys.
- **Statelessness**:
  - Composable functions should be stateless where possible.
  - Use State Hoisting pattern: `Screen(state: State, onAction: (Action) -> Unit)`.
- **Theming**:
  - Verify strict usage of Material3 Design System.
  - No hardcoded colors (`0xFFRRGGBB`) or dimensions outside theme.
  - Use `MaterialTheme.colorScheme` and `MaterialTheme.typography`.

### 3. Security & Fintech Standards

> [!CAUTION]
> Security violations are **blocking issues** and must be fixed before merge.

- **Data Privacy**:
  - **ABSOLUTELY NO** logging of:
    - PII (Personally Identifiable Information)
    - Card Numbers, CVV, or Expiration Dates
    - Transaction Secrets, PINs, or Passwords
- **Sensitive Storage**:
  - Sensitive data must use `EncryptedSharedPreferences` or `SecureCacheStore`.
  - Biometric-backed KeyStore for cryptographic keys.
- **Input Validation**:
  - All financial inputs must be validated before processing.
  - Server-side validation is expected; client-side is defense-in-depth.

### 4. Concurrency & Performance

- **Coroutines**:
  - Verify usage of correct Dispatchers:
    - `Dispatchers.IO` for disk/network operations.
    - `Dispatchers.Default` for CPU-intensive tasks.
    - `Dispatchers.Main` only for UI updates.
  - Use `safeLaunch` in ViewModels for proper error handling.
- **Flow**:
  - Flows must be collected in a lifecycle-aware manner.
  - Use `collectAsStateWithLifecycle()` in Compose screens.
  - Avoid using `collect {}` directly in ViewModel's init block.

### 5. Code Quality

- **Boilerplate**:
  - Identify code that could be simplified using Mason Bricks.
  - Look for repetitive patterns (new features, subfeatures).
- **Testing**:
  - New logic must be accompanied by Unit Tests.
  - Test coverage expected for UseCases and ViewModels.
- **Naming Conventions**:
  - Contract Classes: `[Feature]State`, `[Feature]Action`, `[Feature]Event`.
  - ViewModel: `[Feature]ViewModel`.
  - UseCase: `[Action][Feature]UseCase` (e.g., `GetWalletBalanceUseCase`).
  - DTOs: Must end with `Dto` suffix.

---

## Input

The skill accepts **two input methods**:

| Input Type     | Description                                                    | Command to Fetch                        |
|----------------|----------------------------------------------------------------|-----------------------------------------|
| **COMMIT_ID**  | A specific commit hash to review changes from                  | `git show <COMMIT_ID>` or `git diff <COMMIT_ID>~1 <COMMIT_ID>` |
| **PR_DIFF**    | The Pull Request diff (from GitHub or pasted directly)         | `gh pr diff <PR_NUMBER>`                |

> [!TIP]
> You can provide either one. If a commit ID is provided, the diff will be fetched automatically using `git show`.

---

## Output Format

Your response **MUST** be structured as follows:

```markdown
### 📝 Summary of Changes
(A brief description of what this PR introduces)

### 🚨 Critical Issues (Architecture/Security/Logic)
- **[File Name]:[Line Number]**: [Issue Description] → [Suggested Fix]

### 💡 Refactoring & Style (Compose/Kotlin)
- [Suggestions for cleaner code, better performance, or UI optimization]

### ⚠️ Security Review
- [ ] No PII/sensitive data logged
- [ ] Proper encryption for stored data
- [ ] Input validation present

### ✅ Final Verdict
(Choose one: 🟢 LGTM / 🟡 Needs Work / 🔴 Request Changes)
```

---

## Example Usage

### Option 1: Using Commit ID
```markdown
Use the @pr_review skill to review commit: abc123def
```

### Option 2: Using PR Diff (Pasted)
```markdown
Use the @pr_review skill to review this PR:

<PASTE PR DIFF HERE>
```

### Option 3: Using GitHub CLI
```bash
# Fetch PR diff and copy to clipboard
gh pr diff <PR_NUMBER> | pbcopy

# Then paste into your request
```

### Option 4: Using Git Commands
```bash
# Review a specific commit
git show <COMMIT_ID>

# Review changes between commits
git diff <BASE_COMMIT> <HEAD_COMMIT>
```

