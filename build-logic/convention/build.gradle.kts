plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("compose") {
            id = libs.plugins.situlearner.compose.get().pluginId
            implementationClass = "ComposeConventionPlugin"
        }
    }
    plugins {
        register("feature") {
            id = libs.plugins.situlearner.feature.get().pluginId
            implementationClass = "FeatureConventionPlugin"
        }
    }
    plugins {
        register("koin") {
            id = libs.plugins.situlearner.koin.get().pluginId
            implementationClass = "KoinConventionPlugin"
        }
    }
    plugins {
        register("room") {
            id = libs.plugins.situlearner.room.get().pluginId
            implementationClass = "RoomConventionPlugin"
        }
    }
    plugins {
        register("androidApplication") {
            id = libs.plugins.situlearner.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
    }
    plugins {
        register("androidLibrary") {
            id = libs.plugins.situlearner.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
    plugins {
        register("jvmLibrary") {
            id = libs.plugins.situlearner.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}