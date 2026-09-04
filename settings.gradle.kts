@file:Suppress("UnstableApiUsage")
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AndroidDigitalWallet"
include(":app")
// ── Package modules (grouped under packages/) ──────────────────────────
// Core — the dependency floor (first split of the former framework god-module, epic android_super_app_template)
include(":packages:core")
// Network — the HTTP stack (second split of the former framework god-module, epic android_super_app_template)
include(":packages:network")
// Platform
include(":packages:platform")
// Framework — MVI base + navigation3 host mechanism (epic android_super_app_template)
include(":packages:framework")
// UI Kit — Compose design system + UI helpers; merge of the former components + jetframework library modules (epic android_super_app_template, design §4.1)
include(":packages:ui_kit")
// ── Host ────────────────────────────────────────────────────────────────────
// Shell — Host-only tab shell (ShellViewModel + bottom nav + per-tab nested nav); relocated from features/home (epic android_super_app_template, design §4.1, §9 Phase 2, Task 10)
include(":shell")
// Libraries
include(":libraries:testutils")
// Features — the reusable template keeps only `settings` (real) + `scanner` (empty; becomes a
// dynamic-feature module in Task 14). `home` is a stub page inside `:shell`, not a module.
include(":features:settings")
include(":features:scanner")
// Governance — architecture-enforcement gate (JVM/JUnit, never shipped in the APK)
include(":konsist-test")
