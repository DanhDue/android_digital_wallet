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
// Core — the dependency floor (first split of the former framework god-module, epic android_super_app_template)
include(":core")
// Network — the HTTP stack (second split of the former framework god-module, epic android_super_app_template)
include(":network")
// Platform
include(":platform")
// Framework — MVI base + navigation3 host mechanism (relocated to the repo root, epic android_super_app_template)
include(":framework")
// UI Kit — Compose design system + UI helpers; merge of the former components + jetframework library modules (epic android_super_app_template, design §4.1)
include(":ui_kit")
// Libraries
include(":libraries:testutils")
// Features
include(":features:authentication")
include(":features:settings")
include(":features:trends")
include(":features:scanner")
include(":features:transactions")
include(":features:myWallet")
include(":features:splash")
include(":features:home")
// Governance — architecture-enforcement gate (JVM/JUnit, never shipped in the APK)
include(":konsist-test")
