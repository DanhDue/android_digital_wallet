# Epic Overview: Super App Sandbox Development & Binary Contract Governance

## 1. Meta Data
- **Epic Name:** `sandbox_and_contract_governance`
- **Status:** In Progress
- **Target Release:** 1.0.0
- **Source Spec:** [2026-09-10-sandbox-and-contract-governance-design.md](2026-09-10-sandbox-and-contract-governance-design.md)
- **Architecture Reference:** [docs/architecture/ARCHITECTURE.md](../../../docs/architecture/ARCHITECTURE.md)

---

## 2. Background
In enterprise-scale Super App platforms, independent feature teams ("Mini Apps") require two critical governance capabilities:
1. **Isolated Developer Feedback Loop (Sandbox):** Developers should not need to compile or run the heavy Super App container (`:app` + all sibling features) just to iterate on a single feature. They need a lightweight, standalone application runner (`:features:<name>:sample`) that launches their feature in isolation within seconds.
2. **Strict Public ABI/API Contract Enforcement (BCV):** Foundation packages (`:packages:core`, `:packages:platform`, `:packages:network`, `:packages:framework`, `:packages:ui_kit`) provide the shared interfaces and contracts consumed by all Mini Apps. An unannounced or inadvertent breaking change in public signatures risks fatal runtime crashes (`NoSuchMethodError`, `IncompatibleClassChangeError`) across independently built splits.

This epic introduces both the **Mini App Sandbox Framework** and **Binary Compatibility Validation (BCV)** to enforce lifecycle governance and contract safety across the template.

---

## 3. Goals & Non-Goals

### Goals
- **G1 (Sandbox Plugin):** Provide a standardized `commons.android-sample` convention plugin in `buildSrc` to configure standalone sample runners with zero boilerplate.
- **G2 (Exemplar Implementation):** Build `:features:settings:sample` demonstrating standalone APK compilation, mock hosting, and Navigation 3 entry mounting.
- **G3 (ABI Snapshot Tracking):** Integrate JetBrains' Binary Compatibility Validator (BCV) at the root level, generating `.api` dump files for all 5 shared package modules.
- **G4 (CI Breaking Change Gate):** Wire `apiCheck` into `scripts/acceptance_check.sh` (Phase 6) and `./gradlew check` to block breaking changes before merge.
- **G5 (Mason Brick Generation):** Update `bricks/mvi_feature` to automatically scaffold `sample/` submodules for newly generated Mini Apps.
- **G6 (Konsist Architecture Gate Compliance):** Ensure `:sample` modules strictly respect Konsist rules K1 (no cross-feature imports), K6 (single feature binding), and K8 (no host dependency).

### Non-Goals
- **NG1:** Building standalone runners for on-demand Dynamic Feature Modules (`:features:scanner`) which inherently rely on Google Play Feature Delivery splits.
- **NG2:** Replacing the production `:app` composition root or modifying existing release APK packaging.
- **NG3:** Tracking public API changes inside internal `:features:*` modules (feature internals remain encapsulated; only shared packages are governed).

---

## 4. Architecture & Technical Design

### 4.1 High-Level Architecture
```mermaid
flowchart LR
    subgraph GateCol["🛡️ ARCHITECTURE & CONTRACT GATE"]
        KONSIST["<b>:konsist-test</b><br/>Rules K1–K10<br/>• No cross-feature imports<br/>• Boundary enforcement"]
        BCV["<b>JetBrains BCV</b><br/>./gradlew apiCheck<br/>• Track packages/*.api<br/>• Block breaking ABI changes"]
    end

    subgraph PackageCol["📦 SHARED FOUNDATION PACKAGES"]
        direction TB
        CORE[":packages:core"]
        PLATFORM[":packages:platform"]
        FRAMEWORK[":packages:framework"]
        UIKIT[":packages:ui_kit"]
        NETWORK[":packages:network"]
        FRAMEWORK --> CORE
        NETWORK --> CORE
        UIKIT --> CORE
        PLATFORM --> CORE
    end

    subgraph FeatureCol["🧩 MINI APPS (:features:*)"]
        FEAT_SETTINGS[":features:settings<br/><i>(Install-time Feature)</i>"]
        FEAT_SCANNER[":features:scanner<br/><i>(On-demand DFM)</i>"]
        FEAT_SETTINGS --> PackageCol
        FEAT_SCANNER --> PackageCol
    end

    subgraph SandboxCol["🧪 SANDBOX RUNNERS (:features:*:sample)"]
        SAMPLE_SETTINGS[":features:settings:sample<br/><b>Standalone APK</b><br/>commons.android-sample"]
        SAMPLE_SETTINGS -->|"Mounts ONLY"| FEAT_SETTINGS
        SAMPLE_SETTINGS --> PackageCol
    end

    subgraph HostCol["🏛️ SUPER APP HOST"]
        APP[":app (Composition Root)"]
        SHELL[":shell (Host Tab Shell)"]
        APP --> SHELL
        APP ==> FEAT_SETTINGS
        FEAT_SCANNER -.-> APP
    end

    %% Verification links
    BCV -.->|"Validates Public ABI"| PackageCol
    KONSIST -.->|"Guards Isolation"| FeatureCol
    KONSIST -.->|"Verifies Zero Leaks"| SandboxCol

    %% Styling
    classDef gate fill:#b71c1c,stroke:#e57373,stroke-width:2px,color:#ffffff
    classDef pkg fill:#263238,stroke:#90a4ae,stroke-width:2px,color:#ffffff
    classDef feat fill:#b78103,stroke:#ffd54f,stroke-width:2px,color:#ffffff
    classDef sample fill:#00695c,stroke:#80cbc4,stroke-width:2px,color:#ffffff
    classDef host fill:#1b5e20,stroke:#81c784,stroke-width:2px,color:#ffffff

    class KONSIST,BCV gate
    class CORE,PLATFORM,FRAMEWORK,UIKIT,NETWORK pkg
    class FEAT_SETTINGS,FEAT_SCANNER feat
    class SAMPLE_SETTINGS sample
    class APP,SHELL host
```

### 4.2 Use Cases
```mermaid
flowchart TD
    DEV["Mini App Engineer"]
    PLATFORM_ENG["Platform Engineer"]
    CI["CI / Acceptance Pipeline"]

    subgraph UC_Sandbox["Sandbox Development Flow"]
        U1["Launch :features:settings:sample on Device/Emulator"]
        U2["Fast Hot-Reload & Local MVI State Debugging"]
        U3["Verify Isolated Entry without Sibling Features"]
    end

    subgraph UC_Governance["Contract Governance Flow"]
        U4["Modify Public Class or Interface in Shared Package"]
        U5["Run ./gradlew apiCheck to Detect ABI Breaches"]
        U6["Run ./gradlew apiDump to Approve Intentional API Evolution"]
    end

    DEV --> U1
    DEV --> U2
    DEV --> U3

    PLATFORM_ENG --> U4
    PLATFORM_ENG --> U5
    PLATFORM_ENG --> U6

    CI -->|"Phase 6: apiCheck"| U5
    CI -->|"Konsist Gate K1-K10"| U3
```

### 4.3 Sequence Diagram: Public API Evolution vs. Breaking Change
```mermaid
sequenceDiagram
    autonumber
    actor Dev as Platform Engineer
    participant Git as Git Repo (.api snapshots)
    participant BCV as Gradle apiCheck Task
    participant CI as CI Acceptance Gate

    Note over Dev,Git: Scenario A: Inadvertent Breaking Change
    Dev->>Git: Modifies method signature in :packages:core
    Dev->>BCV: Runs ./gradlew apiCheck
    BCV->>Git: Reads packages/core/api/core.api
    BCV-->>Dev: ❌ FAILS with ABI mismatch diff
    Note over Dev: Dev realizes break and restores backward compatibility

    Note over Dev,Git: Scenario B: Approved Intentional API Addition
    Dev->>Git: Adds new public API method to :packages:platform
    Dev->>BCV: Runs ./gradlew apiDump
    BCV->>Git: Updates packages/platform/api/platform.api with new signature
    Dev->>Git: Commits updated .api file into PR
    Dev->>CI: Pushes branch to GitHub
    CI->>BCV: Runs acceptance_check.sh Phase 6 (apiCheck)
    BCV-->>CI: ✅ PASS (Bytecode matches committed .api)
```

---

## 5. Rollout Strategy & Mitigation

1. **Non-Intrusive Sandbox Scaffolding:** `:sample` modules are pure developer testbeds configured as separate application artifacts (`applicationIdSuffix = ".sample.<feature>"`). They are never aggregated into the production release bundle and do not impact release APK size or security.
2. **ABI Baseline Freezing:** Initial baseline `.api` files are dumped and reviewed before enforcing `apiCheck` on CI, ensuring zero immediate build breakage on clean repositories.
3. **Graceful Fallback:** If an intentional ABI evolution is required, running `./gradlew apiDump` produces transparent Git diffs, allowing architects to inspect public contract changes during peer code review.

---

## 6. Kanban Tasks Breakdown

- [Task 1: Convention Plugin commons.android-sample](../../features/task_1_commons_android_sample_plugin.md)
- [Task 2: Exemplar Sandbox Implementation :features:settings:sample](../../features/task_2_settings_sample_app.md)
- [Task 3: Binary Compatibility Validator (BCV) Integration](../../features/task_3_bcv_plugin_and_api_dump.md)
- [Task 4: CI & Acceptance Script Automation (Phase 6 apiCheck)](../../features/task_4_acceptance_check_phase_6_api_check.md)
- [Task 5: Mason Brick Automation for Sample Modules](../../features/task_5_mason_brick_sample_scaffolding.md)
- [Task 6: Konsist Gate Rules & E2E Verification](../../features/task_6_konsist_scope_and_e2e_verification.md)
