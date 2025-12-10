plugins {
    alias(libs.plugins.situlearner.android.feature)
    alias(libs.plugins.situlearner.ktor)
}

android {
    namespace = "com.coda.situlearner.feature.home.settings.entry"
}

dependencies {
    implementation(projects.core.cfg)
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(libs.androidx.documentfile)
}