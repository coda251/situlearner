plugins {
    alias(libs.plugins.situlearner.feature)
    alias(libs.plugins.situlearner.compose)
}

android {
    namespace = "com.coda.situlearner.feature.home.word.library"
}

dependencies {
    implementation(projects.core.cfg)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.testing)

    implementation(libs.kotlinx.datetime)
}