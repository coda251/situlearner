plugins {
    alias(libs.plugins.situlearner.jvm.library)
    alias(libs.plugins.situlearner.koin)
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.pemistahl.lingua)
}