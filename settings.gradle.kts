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
        maven(url = "https://jitpack.io")
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
include(":feature:home:entry")
include(":feature:home:explore-collection")
include(":feature:home:explore-entry")
include(":feature:home:media-collection")
include(":feature:home:media-entry")
include(":feature:home:settings-chatbot")
include(":feature:home:settings-entry")
include(":feature:home:settings-word")
include(":feature:home:word-book")
include(":feature:home:word-entry")
include(":feature:player:entry")
include(":feature:player:playlist")
include(":feature:player:word")
include(":feature:word:detail-edit")
include(":feature:word:detail-entry")
include(":feature:word:detail-relation")
include(":feature:word:list-echo")
include(":feature:word:list-entry")
include(":feature:word:quiz-entry")
include(":feature:word:quiz-meaning")
include(":feature:word:quiz-translation")
include(":infra:chatbot")
include(":infra:explorer:local")
include(":infra:player")
include(":infra:subkit:lang-detector")
include(":infra:subkit:matcher")
include(":infra:subkit:parser")
include(":infra:subkit:processor")
include(":infra:subkit:tokenizer")
include(":infra:subkit:translator")