# Remove Facebook Flipper & SoLoader Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Completely eliminate Facebook Flipper and SoLoader to achieve 100% 16 KB page size alignment on `app-debug.apk` and decouple `:packages:framework` from `:packages:network`.

**Architecture:** Remove Flipper initializers, reflection bridges, and backstack observers; update `buildSrc` dependency catalogs and extension functions; preserve in-app logging via Chucker; update canonical architecture docs and diagrams.

**Tech Stack:** Kotlin 2.1, Gradle Kotlin DSL, Jetpack Compose, Navigation 3, Dagger Hilt.

**Spec:** [docs/architecture/ARCHITECTURE.md](file:///Users/danhdueexoictif/AllProjects/digital_wallet/android_digital_wallet/docs/architecture/ARCHITECTURE.md)

## Global Constraints
- Do not hardcode versions in `build.gradle.kts`; manage via `Versions.kt` and `Deps.kt`.
- Preserve Konsist K1–K10 gate compliance (run `./gradlew :konsist-test:test`).
- Pass Detekt, Spotless, and unit test checks cleanly.
- Symmetrical updates across Vietnamese and English documentation.

---

### Task 1: Clean Central Dependency Management (`buildSrc`)

**Files:**
- Modify: `buildSrc/src/main/kotlin/Versions.kt`
- Modify: `buildSrc/src/main/kotlin/Deps.kt`
- Modify: `buildSrc/src/main/kotlin/extensions/DependencyHandlerExtensions.kt`

- [ ] **Step 1: Remove Flipper and SoLoader from `Versions.kt`**
  Remove `const val flipper` and `const val soLoader`.
- [ ] **Step 2: Remove Flipper catalogs from `Deps.kt`**
  Remove `object Flipper` and `object FlipperPlugins`.
- [ ] **Step 3: Update `DependencyHandlerExtensions.kt`**
  Remove `addFlipperDependencies()`, remove `Deps.FlipperPlugins.leakCanary` from `addLeakCanaryDependencies()`, and update doc comments.

---

### Task 2: Remove Flipper from Network Module (`:packages:network`)

**Files:**
- Delete: `packages/network/src/main/kotlin/com/danhdue/network/base/app/FlipperInitializer.kt`
- Delete: `packages/network/src/main/kotlin/com/danhdue/network/flipper/FlipperNavigationObject.kt`
- Delete: `packages/network/src/main/kotlin/com/danhdue/network/flipper/FlipperNetworkObject.kt`
- Modify: `packages/network/src/main/kotlin/com/danhdue/network/di/NetworkCoreModule.kt`
- Modify: `packages/network/build.gradle.kts`

- [ ] **Step 1: Delete Flipper sources**
  Delete `FlipperInitializer.kt`, `FlipperNavigationObject.kt`, `FlipperNetworkObject.kt`, and the directory `packages/network/src/main/kotlin/com/danhdue/network/flipper/`.
- [ ] **Step 2: Update `NetworkCoreModule.kt`**
  Remove `FlipperNetworkObject` import and interceptor hook from OkHttp client builder.
- [ ] **Step 3: Update `packages/network/build.gradle.kts`**
  Remove `import extensions.addFlipperDependencies`, remove `addFlipperDependencies()`, and update module comments.

---

### Task 3: Remove Flipper from Framework Module (`:packages:framework`) & Decouple from Network

**Files:**
- Delete: `packages/framework/src/main/java/com/danhdue/framework/navigation/FlipperBackstackObserver.kt`
- Modify: `packages/framework/src/main/java/com/danhdue/framework/navigation/Navigator.kt`
- Modify: `packages/framework/build.gradle.kts`

- [ ] **Step 1: Delete `FlipperBackstackObserver.kt`**
  Remove `FlipperBackstackObserver.kt`.
- [ ] **Step 2: Clean `Navigator.kt` doc comments**
  Remove reference to `ObserveBackstackForFlipper`.
- [ ] **Step 3: Update `packages/framework/build.gradle.kts`**
  Remove `addFlipperDependencies()`, remove `implementation(project(":packages:network"))`, and update comments to declare `:framework` only depends on `:core`.

---

### Task 4: Remove Flipper Invocations from App and Shell Modules (`:app`, `:shell`)

**Files:**
- Modify: `shell/src/main/kotlin/com/danhdue/shell/ShellScreen.kt`
- Modify: `app/src/main/kotlin/com/danhdue/androiddigitalwallet/ui/MainActivity.kt`
- Modify: `app/src/main/kotlin/com/danhdue/androiddigitalwallet/di/AppModule.kt`
- Modify: `app/build.gradle.kts`

- [ ] **Step 1: Update `ShellScreen.kt`**
  Remove `ObserveBackstackForFlipper` import and invocation.
- [ ] **Step 2: Update `MainActivity.kt`**
  Remove `ObserveBackstackForFlipper` import and invocation.
- [ ] **Step 3: Update `AppModule.kt`**
  Remove `FlipperInitializer` import, remove `providesFlipperInitializer()`, and remove it from `providesAppInitializer(...)`.
- [ ] **Step 4: Update `app/build.gradle.kts`**
  Clean up comments referencing `FlipperInitializer`.

---

### Task 5: Documentation & Architecture Diagram Updates

**Files:**
- Delete: `docs/flipper_integration.md`
- Modify: `AGENTS.md`
- Modify: `README.md`
- Modify: `docs/architecture/ARCHITECTURE.md`
- Modify: `.devtool/epic/android_super_app_template/android_super_app_template.en.md`
- Modify: `.devtool/epic/android_super_app_template/android_super_app_template.vi.md`

- [ ] **Step 1: Delete `docs/flipper_integration.md`**
- [ ] **Step 2: Update `AGENTS.md` & `README.md`**
  Remove Flipper mentions from module descriptions.
- [ ] **Step 3: Update `docs/architecture/ARCHITECTURE.md`**
  Remove Flipper from tables and remove `FRAMEWORK --> NETWORK` link from architecture diagram.
- [ ] **Step 4: Update English and Vietnamese epic documents**
  Synchronize diagrams in `android_super_app_template.en.md` and `android_super_app_template.vi.md`.

---

### Task 6: Build Verification & 16 KB Alignment Check

- [ ] **Step 1: Run Konsist and unit tests**
  `./gradlew :konsist-test:test testDebugUnitTest`
- [ ] **Step 2: Run Detekt and Spotless**
  `./gradlew detekt spotlessCheck`
- [ ] **Step 3: Assemble Debug APK**
  `./gradlew assembleDebug`
- [ ] **Step 4: Verify 16 KB ELF alignment**
  Execute python inspection script on `./app/build/outputs/apk/debug/app-debug.apk` to ensure zero unaligned `.so` libraries exist.
