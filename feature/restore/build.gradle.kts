plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.restore"
}

dependencies {
    implementation(projects.core.cfg)
    implementation(projects.core.model)
}