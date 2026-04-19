plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.word.quiz.entry"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)

    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
}