plugins {
    alias(libs.plugins.compose.compiler).apply(false)
}

buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.android.gradle)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.kotlin.serialization)
    }
}
