plugins {
    alias(libs.plugins.situlearner.jvm.library)
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.jsoup)
    implementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
}