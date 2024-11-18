plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp") version libs.versions.ksp.get()
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.ramcosta.destinations.sample"
    compileSdk = libs.versions.sdk.compile.get().toIntOrNull()

    defaultConfig {
        applicationId = "com.ramcosta.destinations.sample"
        minSdk = libs.versions.sdk.min.get().toIntOrNull()
        targetSdk = libs.versions.sdk.target.get().toIntOrNull()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
}

kotlin {
    jvmToolchain(11)
    compilerOptions {
        freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    implementation(project(mapOf("path" to ":compose-destinations")))
    implementation(project(mapOf("path" to ":compose-destinations-bottom-sheet")))
    ksp(project(":compose-destinations-ksp"))

    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)
    implementation(libs.androidx.icons)
    implementation(libs.lifecycle.viewmodel.compose)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
}
