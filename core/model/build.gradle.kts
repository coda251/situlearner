plugins {
    alias(libs.plugins.situlearner.jvm.library)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    api(libs.kotlinx.datetime)
    implementation(libs.androidx.annotation.jvm)
}