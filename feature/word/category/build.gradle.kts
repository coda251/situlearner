plugins {
    alias(libs.plugins.situlearner.feature)
    alias(libs.plugins.situlearner.compose)
}

android {
    namespace = "com.coda.situlearner.feature.word.category"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.testing)

    implementation(libs.kotlinx.datetime)
}