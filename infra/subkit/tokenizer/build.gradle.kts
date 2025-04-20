plugins {
    alias(libs.plugins.situlearner.jvm.library)
}

dependencies {
    implementation(projects.core.model)

    // for ja tokenizer
    implementation(libs.kuromoji.ipadic)
    // for en tokenizer
    implementation(files("libs/opennlp-tools-2.5.4-SNAPSHOT.jar"))
    implementation(libs.slf4j.api)

    implementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
}