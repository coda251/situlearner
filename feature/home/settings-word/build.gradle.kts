plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.home.settings.word"
}

dependencies {
    implementation(projects.core.cfg)
    implementation(projects.core.data)
    implementation(projects.core.model)
}