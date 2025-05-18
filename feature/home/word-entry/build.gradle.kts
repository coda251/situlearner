plugins {
    alias(libs.plugins.situlearner.feature)
    alias(libs.plugins.situlearner.compose)
}

android {
    namespace = "com.coda.situlearner.feature.home.word.entry"
}

dependencies {
    implementation(projects.core.cfg)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.testing)
}