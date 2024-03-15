import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("ca.cutterslade.analyze") version "1.9.2"
}

val keystorePropertiesFile: File = rootProject.file("key.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.noisevisionproductions.playmeet"
    compileSdk = 34

    signingConfigs {
        create("config") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    packaging {
        resources {
            pickFirsts.add("META-INF/AL2.0")
            pickFirsts.add("META-INF/LGPL2.1")
            pickFirsts.add("META-INF/native-image/org.mongodb/bson/native-image.properties")
        }
    }

    defaultConfig {
        applicationId = "com.noisevisionproductions.playmeet"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "1.4.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("config")
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.paging:paging-runtime:3.2.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-firestore:24.10.3")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.firebaseui:firebase-ui-database:8.0.2")
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")
    implementation("com.google.firebase:firebase-analytics:21.5.1")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.android.gms:play-services-identity:18.0.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.navigation:navigation-common:2.7.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}