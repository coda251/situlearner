plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.word.quiz.translation"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.infra.chatbot)

    implementation(libs.compose.markdown)
}