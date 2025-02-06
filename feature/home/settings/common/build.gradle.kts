plugins {
    alias(libs.plugins.situlearner.feature)
    alias(libs.plugins.situlearner.compose)
}

android {
    namespace = "com.coda.situlearner.feature.home.settings.common"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
}