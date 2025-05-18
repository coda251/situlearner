plugins {
    alias(libs.plugins.situlearner.feature)
    alias(libs.plugins.situlearner.compose)
    alias(libs.plugins.situlearner.ktor)
}

android {
    namespace = "com.coda.situlearner.feature.home.settings.common"
}

dependencies {
    implementation(projects.core.cfg)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.network)
}