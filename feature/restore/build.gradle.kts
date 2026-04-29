plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.restore"
}

dependencies {
    implementation(projects.core.model)
}