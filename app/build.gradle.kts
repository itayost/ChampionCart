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

    // Updated to JDK 17 for better compatibility
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

    composeOptions {
        // Ensure Compose compiler extension version matches Kotlin
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Additional excludes to prevent conflicts
            excludes += "/META-INF/gradle/incremental.annotation.processors"
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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)  // Updated version
    implementation(libs.androidx.lifecycle.runtime.compose)   // Updated version
    implementation(libs.androidx.material.icons.extended)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Dependency Injection - Hilt
    implementation(libs.hilt.android)      // Updated to latest stable
    ksp(libs.hilt.compiler)               // Updated to latest stable
    implementation(libs.androidx.hilt.navigation.compose)  // Updated version

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)  // Updated version

    // Image Loading
    implementation(libs.coil.compose)

    // Accompanist libraries for modern UI
    implementation(libs.accompanist.systemuicontroller)

    // Gson for JSON parsing
    implementation(libs.gson)

    // Lottie animations for your design system
    implementation(libs.lottie.compose)

    // For advanced visual effects
    implementation(libs.ui.graphics)
    implementation(libs.androidx.animation.graphics)

    // SplashScreen API
    implementation(libs.androidx.core.splashscreen)

    // Advanced Material3 components
    implementation(libs.androidx.material3.window.size.class1)  // Updated version

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