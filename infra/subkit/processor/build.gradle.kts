plugins {
    alias(libs.plugins.situlearner.jvm.library)
    alias(libs.plugins.situlearner.koin)
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.infra.subkit.tokenizer)
    implementation(projects.infra.subkit.langDetector)
    implementation(projects.infra.subkit.parser)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okio)
}