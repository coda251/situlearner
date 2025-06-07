plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.home.settings.chatbot"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
}