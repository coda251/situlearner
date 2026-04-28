plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.player.fullscreen"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.infra.player)
    implementation(projects.infra.subkit.processor)
}