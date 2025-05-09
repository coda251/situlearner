plugins {
    alias(libs.plugins.situlearner.android.library)
    alias(libs.plugins.situlearner.koin)
    alias(libs.plugins.situlearner.compose)
}

android {
    namespace = "com.coda.situlearner.infra.player"
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui.compose)
}