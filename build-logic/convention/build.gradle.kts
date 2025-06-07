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
        register("androidApplication") {
            id = libs.plugins.situlearner.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.situlearner.android.feature.get().pluginId
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.situlearner.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("compose") {
            id = libs.plugins.situlearner.compose.get().pluginId
            implementationClass = "ComposeConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.situlearner.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("koin") {
            id = libs.plugins.situlearner.koin.get().pluginId
            implementationClass = "KoinConventionPlugin"
        }
        register("ktor") {
            id = libs.plugins.situlearner.ktor.get().pluginId
            implementationClass = "KtorConventionPlugin"
        }
        register("room") {
            id = libs.plugins.situlearner.room.get().pluginId
            implementationClass = "RoomConventionPlugin"
        }
    }
}