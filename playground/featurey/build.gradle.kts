plugins {
    id("com.android.library")
    kotlin("android")
    id("com.google.devtools.ksp") version libs.versions.ksp.get()
    kotlin("plugin.serialization")
    alias(libs.plugins.compose.compiler)
}

android {

    namespace = "com.ramcosta.playground.featurey"
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

    ksp {
//        arg("compose-destinations.debugMode", "$rootDir")
        arg("compose-destinations.moduleName", "featureY")
        arg("compose-destinations.htmlMermaidGraph", "$rootDir/playground/docs")
        arg("compose-destinations.mermaidGraph", "$rootDir/playground/docs")
    }
}

kotlin {
    jvmToolchain(8)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
}

dependencies {

    implementation(project(mapOf("path" to ":playground:featurey:sub")))
    implementation(project(mapOf("path" to ":compose-destinations")))
    ksp(project(":compose-destinations-ksp"))

    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)
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
