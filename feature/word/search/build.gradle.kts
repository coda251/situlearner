plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.word.search"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.infra.player)
    implementation(projects.infra.subkit.processor)
}