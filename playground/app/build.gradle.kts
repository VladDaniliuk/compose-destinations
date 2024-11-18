plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp") version libs.versions.ksp.get()
    kotlin("plugin.parcelize")
    kotlin("plugin.serialization")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.ramcosta.samples.playground"
    compileSdk = libs.versions.sdk.compile.get().toIntOrNull()

    defaultConfig {
        applicationId = "com.ramcosta.samples.playground"
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

    // Possible Compose Destinations configs:
    ksp {
//        arg("compose-destinations.debugMode", "$rootDir")
//        // Module name.
//        // It will be used as the generated sealed Destinations prefix
//        arg("compose-destinations.moduleName", "featureX")

//        // If you want to manually create the nav graphs, use this:
//        arg("compose-destinations.generateNavGraphs", "false")
        arg("compose-destinations.htmlMermaidGraph", "$rootDir/playground/docs")
        arg("compose-destinations.mermaidGraph", "$rootDir/playground/docs")

        // To change the package name where the generated files will be placed
        arg("compose-destinations.codeGenPackageName", "com.ramcosta.samples.playground.ui.screens")
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
    implementation(project(mapOf("path" to ":playground:core")))
    implementation(project(mapOf("path" to ":playground:featurex")))
    implementation(project(mapOf("path" to ":playground:featurey")))
    implementation(project(mapOf("path" to ":playground:featurez")))
    ksp(project(":compose-destinations-ksp"))

    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)
    implementation(libs.androidx.icons)
    implementation(libs.lifecycle.viewmodel.compose)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)

    testImplementation(project(":compose-destinations-ksp"))
    testImplementation(libs.kotlin.compile.testing)
    testImplementation(libs.kotlin.compile.testing.ksp)
}
