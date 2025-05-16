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
        versionName = "0.5.0-alpha04"
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
    implementation(projects.core.ui)

    implementation(projects.feature.home)
    implementation(projects.feature.player.entry)
    implementation(projects.feature.player.playlist)
    implementation(projects.feature.player.word)
    implementation(projects.feature.word.list)
    implementation(projects.feature.word.detail)
    implementation(projects.feature.word.echo)
    implementation(projects.feature.word.edit)
    implementation(projects.feature.word.quiz.meaning)

    implementation(projects.infra.explorerLocal)
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