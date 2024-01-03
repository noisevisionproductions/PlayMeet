val realmAppId: String = project.properties["REALM_APP_ID"] as String

fun pickFirst() {
    TODO("Not yet implemented")
}

realm {
    isSyncEnabled = true
}

plugins {
    id("com.android.application")
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
            buildConfigField("String", "RealmAppId", "\"${realmAppId}\"")
        }
        release {
            buildConfigField("String", "RealmAppId", "\"${realmAppId}\"")

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
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("androidx.navigation:navigation-common:2.7.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}