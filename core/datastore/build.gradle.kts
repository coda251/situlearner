plugins {
    alias(libs.plugins.situlearner.android.library)
    alias(libs.plugins.situlearner.koin)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.coda.situlearner.core.datastore"
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
}

dependencies {
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.kotlinlite)

    implementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.junit)
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}