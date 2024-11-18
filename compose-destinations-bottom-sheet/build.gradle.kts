plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.compose.compiler)
}

android {

    namespace = "com.ramcosta.composedestinations.bottomsheet"
    compileSdk = libs.versions.sdk.compile.get().toIntOrNull()

    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toIntOrNull()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles.add(File("consumer-rules.pro"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=com.ramcosta.composedestinations.annotation.internal.InternalDestinationsApi"
        )
    }
}

dependencies {

    implementation(project(mapOf("path" to ":compose-destinations")))

    api(libs.material.navigation)
    api(libs.androidx.material)
}
