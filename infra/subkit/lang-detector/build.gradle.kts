plugins {
    alias(libs.plugins.situlearner.jvm.library)
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.pemistahl.lingua)
}