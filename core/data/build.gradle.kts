plugins {
    alias(libs.plugins.situlearner.android.library)
    alias(libs.plugins.situlearner.koin)
}

android {
    namespace = "com.coda.situlearner.core.data"
}

dependencies {
    implementation(projects.core.cache)
    implementation(projects.core.cfg)
    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.datastore)

    implementation(libs.androidx.core.ktx)
    implementation(libs.protobuf.kotlinlite)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
}