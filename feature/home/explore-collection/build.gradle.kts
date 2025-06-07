plugins {
    alias(libs.plugins.situlearner.android.feature)
}

android {
    namespace = "com.coda.situlearner.feature.home.explore.collection"
}

dependencies {
    implementation(projects.core.cfg)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.infra.explorer.local)
    implementation(projects.infra.subkit.langDetector)
    implementation(projects.infra.subkit.tokenizer)
    implementation(projects.infra.subkit.processor)
}