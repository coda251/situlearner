plugins {
    alias(libs.plugins.situlearner.feature)
    alias(libs.plugins.situlearner.compose)
}

android {
    namespace = "com.coda.situlearner.feature.word.quiz.entry"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
}