@file:Suppress("UnstableApiUsage")

include(":features:settings")


include(":features:trends")


include(":features:scanner")


include(":features:transactions")


include(":features:myWallet")


include(":features:splash")


include(":features:home")

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
// Libraries
include(":libraries:framework")
include(":libraries:jetframework")
include(":libraries:testutils")
include(":libraries:components")
// Data
include(":data:repositories")
include(":data:remote")
include(":data:model")
include(":data:local")
// Domain
include(":domain:authenticator")