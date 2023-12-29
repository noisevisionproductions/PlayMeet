fun pickFirst() {
    TODO("Not yet implemented")
}

realm {
    isSyncEnabled = true
}

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("realm-android")
}

android {
    namespace = "com.example.zagrajmy"
    compileSdk = 34


    packaging {
        resources {
            pickFirsts.add("META-INF/native-image/org.mongodb/bson/native-image.properties")
        }
    }

    defaultConfig {
        applicationId = "com.example.zagrajmy"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "RealmAppId", "\"${System.getenv("REALM_APP_ID")}\"")
        }
        release {
            buildConfigField("String", "RealmAppId", "\"${System.getenv("REALM_APP_ID")}\"")

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
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.navigation:navigation-ui:2.7.5")
    implementation("androidx.navigation:navigation-common:2.7.5")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}