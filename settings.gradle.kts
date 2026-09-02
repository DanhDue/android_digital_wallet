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
// Platform
include(":platform")
// Libraries
include(":libraries:framework")
include(":libraries:jetframework")
include(":libraries:testutils")
include(":libraries:components")
// Domain
include(":domain:authenticator")
// Features
include(":features:authentication")
include(":features:settings")
include(":features:trends")
include(":features:scanner")
include(":features:transactions")
include(":features:myWallet")
include(":features:splash")
include(":features:home")
