plugins {
    alias(libs.plugins.situlearner.android.library)
    alias(libs.plugins.situlearner.compose)
}

android {
    namespace = "com.coda.situlearner.core.ui"
}

dependencies {
    implementation(projects.core.model)

    api(libs.androidx.compose.material3)
    api(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.materialKolor)
}