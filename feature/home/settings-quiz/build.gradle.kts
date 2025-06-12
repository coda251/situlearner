plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.home.settings.quiz"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
}