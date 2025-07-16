plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.citygame"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.citygame"
        minSdk = 24
        targetSdk = 33
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom =
        platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    debugImplementation("androidx.compose.ui:ui-tooling:1.8.3")

    // Worker (used for notification)
    implementation("androidx.work:work-runtime-ktx:2.10.2")

    val nav_version = "2.7.7"
    val maps_compose_version = "4.3.3"
    val play_services_version = "21.3.0"
    val sceneview_version = "2.1.1"
    val camerax_version = "1.3.3"
    val lifecycle_version = "2.8.2"
    implementation("androidx.compose.material:material-icons-extended:1.6.7")

    implementation("com.google.mediapipe:tasks-vision:latest.release")

    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")

    // Online
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // Internal storage
    implementation("androidx.room:room-runtime:2.7.2")


    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    implementation("androidx.core:core-ktx:1.13.1")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")


    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    testImplementation("junit:junit:4.13.2")

    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation("com.google.maps.android:maps-compose:$maps_compose_version")
    implementation("com.google.android.gms:play-services-location:$play_services_version")

    implementation("io.github.sceneview:sceneview:$sceneview_version")

    implementation("io.coil-kt:coil-compose:2.7.0") //For AsyncImages

    // WebSocket protocol
    implementation("io.socket:socket.io-client:2.0.1")

}