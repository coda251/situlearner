plugins {
    alias(libs.plugins.situlearner.android.library)
    alias(libs.plugins.situlearner.koin)
}

android {
    namespace = "com.coda.situlearner.infra.subkit.tokenizer"
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
}

dependencies {
    implementation(projects.core.model)

    // for ja tokenizer
    implementation(libs.kuromoji.ipadic)
    // for en tokenizer
    implementation(files("libs/opennlp-tools-2.5.4-SNAPSHOT.jar"))
    implementation(libs.slf4j.api)

    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.test.core.ktx)
    testImplementation(libs.junit)

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.runner)
}