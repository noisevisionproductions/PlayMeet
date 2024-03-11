buildscript {
    repositories {
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.android.tools.build:gradle:8.3.0")
    }
}

plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.autonomousapps.dependency-analysis") version "1.29.0"
}
