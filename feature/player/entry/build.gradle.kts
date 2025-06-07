plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.player.entry"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.infra.player)
    implementation(projects.infra.subkit.processor)
}