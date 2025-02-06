plugins {
    alias(libs.plugins.situlearner.feature)
    alias(libs.plugins.situlearner.compose)
}

android {
    namespace = "com.coda.situlearner.feature.player.entry"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.cfg)
    implementation(projects.core.model)
    implementation(projects.infra.player)
    implementation(projects.infra.subkit.processor)
    implementation(projects.infra.subkit.translator)
    implementation(projects.core.testing)

    implementation(libs.kotlinx.datetime)
}