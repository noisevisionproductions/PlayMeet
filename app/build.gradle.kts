fun pickFirst() {
    TODO("Not yet implemented")
}

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.noisevisionproductions.playmeet"
    compileSdk = 34

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
        versionCode = 1
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.firebaseui:firebase-ui-database:8.0.0")
    implementation("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.gms:google-services:4.4.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-identity:18.0.1")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("androidx.navigation:navigation-common:2.7.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}