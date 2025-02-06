plugins {
    alias(libs.plugins.situlearner.jvm.library)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
}