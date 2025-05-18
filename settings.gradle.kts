enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SituLearner"
include(":app")
include(":core:cache")
include(":core:cfg")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:model")
include(":core:network")
include(":core:testing")
include(":core:ui")
include(":infra:chatbot")
include(":infra:explorer-local")
include(":infra:player")
include(":infra:subkit:lang-detector")
include(":infra:subkit:parser")
include(":infra:subkit:processor")
include(":infra:subkit:tokenizer")
include(":infra:subkit:translator")
include(":feature:home")
include(":feature:home:media:collection")
include(":feature:home:media:library")
include(":feature:home:explore:collection")
include(":feature:home:explore:library")
include(":feature:home:word:book")
include(":feature:home:word:library")
include(":feature:home:settings:common")
include(":feature:home:settings:chatbot")
include(":feature:player:entry")
include(":feature:player:playlist")
include(":feature:player:word")
include(":feature:word:detail")
include(":feature:word:echo")
include(":feature:word:edit")
include(":feature:word:list")
include(":feature:word:quiz:meaning")
include(":feature:word:quiz:sentence")