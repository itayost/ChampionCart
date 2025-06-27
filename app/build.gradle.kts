plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}

android {
    namespace = "com.example.championcart"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.championcart"
        minSdk = 28
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    // Core Android & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation & Lifecycle
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Dependency Injection - Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    ksp("com.google.dagger:hilt-compiler:2.48.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Accompanist libraries for modern UI
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // Lottie animations for your design system
    implementation("com.airbnb.android:lottie-compose:6.3.0")

    // For advanced visual effects
    implementation("androidx.compose.ui:ui-graphics:1.5.4")
    implementation("androidx.compose.animation:animation-graphics:1.5.4")

    // SplashScreen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Advanced Material3 components
    implementation("androidx.compose.material3:material3-window-size-class:1.1.2")

    // FUTURE ADDITIONS (uncomment when needed):

    // Room Database (when implementing local caching)
    // implementation("androidx.room:room-runtime:2.6.1")
    // implementation("androidx.room:room-ktx:2.6.1")
    // ksp("androidx.room:room-compiler:2.6.1")

    // DataStore (if switching from SharedPreferences)
    // implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Paging 3 (when implementing large product lists)
    // implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    // implementation("androidx.paging:paging-compose:3.2.1")

    // CameraX & ML Kit (when implementing barcode scanning)
    // implementation("androidx.camera:camera-camera2:1.3.1")
    // implementation("androidx.camera:camera-lifecycle:1.3.1")
    // implementation("androidx.camera:camera-view:1.3.1")
    // implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // Location Services (when implementing store locator)
    // implementation("com.google.android.gms:play-services-location:21.0.1")
    // implementation("com.google.android.gms:play-services-maps:18.2.0")
}