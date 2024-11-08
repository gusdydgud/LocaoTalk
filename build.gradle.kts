// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15") // Google Services 플러그인 추가
        classpath("com.android.tools.build:gradle:7.4.2") // Java 11 호환 버전
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
