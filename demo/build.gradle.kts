import util.ProjectConfig

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(project(":library"))

    // Misc
    implementation(libs.core.ktx)
    implementation(libs.appcompat)

    // Jetpack Compose UI
    implementation(libs.ui)
    implementation(libs.ui.tooling)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.material.icons.core)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
}

android {
    namespace = "com.devbrackets.android.datastoredemo"

    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        applicationId = "com.devbrackets.android.datastoredemo"
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
        versionCode = 1
        versionName = "1.0.0-demo"
    }

    sourceSets {
        getByName("main") {
            kotlin.directories.add("src/main/kotlin")
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = ProjectConfig.javaVersion
        targetCompatibility = ProjectConfig.javaVersion
    }

    lint {
        abortOnError = false
    }
}
