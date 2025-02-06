plugins {
    alias(libs.plugins.situlearner.android.library)
    alias(libs.plugins.situlearner.koin)
}

android {
    namespace = "com.coda.situlearner.infra.explorer_local"
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.okio)
}