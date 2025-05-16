plugins {
    alias(libs.plugins.situlearner.feature)
    alias(libs.plugins.situlearner.compose)
}

android {
    namespace = "com.coda.situlearner.feature.home"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.infra.player)

    implementation(projects.feature.home.explore.library)
    implementation(projects.feature.home.explore.collection)
    implementation(projects.feature.home.media.library)
    implementation(projects.feature.home.media.collection)
    implementation(projects.feature.home.settings.common)
    implementation(projects.feature.home.settings.chatbot)
    implementation(projects.feature.home.word.library)
    implementation(projects.feature.home.word.book)
}