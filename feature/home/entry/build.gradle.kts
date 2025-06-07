plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.home.entry"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.infra.player)

    implementation(projects.feature.home.exploreEntry)
    implementation(projects.feature.home.exploreCollection)
    implementation(projects.feature.home.mediaEntry)
    implementation(projects.feature.home.mediaCollection)
    implementation(projects.feature.home.settingsEntry)
    implementation(projects.feature.home.settingsChatbot)
    implementation(projects.feature.home.wordEntry)
    implementation(projects.feature.home.wordBook)
}