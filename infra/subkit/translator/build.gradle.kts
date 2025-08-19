plugins {
    alias(libs.plugins.situlearner.jvm.library)
    alias(libs.plugins.situlearner.ktor)
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.jsoup)
    implementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
}