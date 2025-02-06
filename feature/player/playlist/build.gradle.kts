plugins {
    alias(libs.plugins.situlearner.feature)
    alias(libs.plugins.situlearner.compose)
}

android {
    namespace = "com.coda.situlearner.feature.player.playlist"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.infra.player)

    implementation(libs.reorderable)
}