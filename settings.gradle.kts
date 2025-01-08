pluginManagement {
    repositories {
        includeBuild("build-logic")
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
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "QuestWalk"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")

include(":feature:login")

include(":core:designsystem")
include(":core:navigation")
include(":core:domain")

include(
    ":core:designsystem",
    ":core:navigation",
    ":core:domain",
    ":core:model",
)