plugins {
    alias(libs.plugins.situlearner.android.application)
    alias(libs.plugins.situlearner.koin)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.coda.situlearner"

    defaultConfig {
        applicationId = "com.coda.situlearner"
        versionCode = 5
        versionName = "0.5.0-alpha05"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/*.md"
        }
    }
}

dependencies {
    implementation(projects.core.cache)
    implementation(projects.core.cfg)
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.ui)

    implementation(projects.feature.home.entry)
    implementation(projects.feature.player.entry)
    implementation(projects.feature.player.playlist)
    implementation(projects.feature.player.word)
    implementation(projects.feature.word.detailEntry)
    implementation(projects.feature.word.detailEdit)
    implementation(projects.feature.word.listEntry)
    implementation(projects.feature.word.listEcho)
    implementation(projects.feature.word.quizEntry)
    implementation(projects.feature.word.quizMeaning)
    implementation(projects.feature.word.quizTranslation)

    implementation(projects.infra.explorer.local)
    implementation(projects.infra.player)
    implementation(projects.infra.subkit.processor)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.navigation)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}