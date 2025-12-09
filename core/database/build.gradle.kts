plugins {
    alias(libs.plugins.situlearner.android.library)
    alias(libs.plugins.situlearner.room)
    alias(libs.plugins.situlearner.koin)
}

android {
    namespace = "com.coda.situlearner.core.database"

    defaultConfig {
        testInstrumentationRunner = "com.coda.situlearner.core.database.InstrumentationTestRunner"
    }
}

dependencies {
    implementation(projects.core.cfg)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.koin.android)
    androidTestImplementation(libs.androidx.test.runner)
}