plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.home.media.edit"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.infra.explorer.local)
}