plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.player.word"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.cfg)
    implementation(projects.core.testing)
    implementation(projects.infra.subkit.translator)
    implementation(projects.infra.player)

    implementation(libs.androidx.compose.material.navigation)
}