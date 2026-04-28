plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.home.settings.entry"
}

dependencies {
    implementation(projects.core.cfg)
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.model)
    implementation(libs.androidx.documentfile)
    implementation(libs.compose.markdown)
}