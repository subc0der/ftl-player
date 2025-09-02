<<<<<<< HEAD
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
=======
// ╔══════════════════════════════════════════════════════════════╗
// ║           FTL HI-RES AUDIO PLAYER - BUILD CONFIG           ║
// ║              Neural-Enhanced Audiophile Grade               ║
// ╚══════════════════════════════════════════════════════════════╝
//
// 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
//
// Performance Targets:
// • Audio Latency: <10ms total pipeline
// • THD+N: <0.001% @ 1kHz, -60dB
// • Sample Rates: Up to 768kHz/32-bit
// • DSD Support: DSD64, DSD128, DSD256, DSD512

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
>>>>>>> feature/core-audio-engine
}

android {
    namespace = "com.ftl.audioplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ftl.audioplayer"
<<<<<<< HEAD
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Native library configuration for audio engine
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64")
        }
        
        // External native build configuration
        // Temporarily disabled until audio engine is implemented
        // externalNativeBuild {
        //     cmake {
        //         cppFlags += "-std=c++17"
        //         arguments += "-DANDROID_STL=c++_shared"
        //     }
        // }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    
=======
        minSdk = 26  // Android 8.0 - AAudio support required
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0-alpha"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Native library configuration for audio engine
        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
        }
        
        // External native build configuration
        externalNativeBuild {
            cmake {
                cppFlags.addAll(listOf(
                    "-std=c++17",
                    "-ffast-math",
                    "-O3",
                    "-DNDEBUG",
                    "-funroll-loops",
                    "-DTARGET_LATENCY_MS=10"
                ))
                arguments.addAll(listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_PLATFORM=android-26"
                ))
            }
        }
    }
    
    // External native build configuration
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            
            // Debug-specific native flags
            externalNativeBuild {
                cmake {
                    cppFlags.add("-DDEBUG_AUDIO_ENGINE")
                }
            }
        }
        
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Release optimization flags
            externalNativeBuild {
                cmake {
                    cppFlags.addAll(listOf(
                        "-DRELEASE_BUILD",
                        "-flto",
                        "-fomit-frame-pointer"
                    ))
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.ExperimentalStdlibApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }

>>>>>>> feature/core-audio-engine
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
<<<<<<< HEAD
    
    // External native build configuration for C++ audio engine
    // Temporarily disabled until audio engine is implemented
    // externalNativeBuild {
    //     cmake {
    //         path = file("src/main/cpp/CMakeLists.txt")
    //     }
    // }

    // TensorFlow Lite configuration
    aaptOptions {
        noCompress += "tflite"
        noCompress += "lite"
    }
}

dependencies {
    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.0")
    
    // Jetpack Compose BOM and UI
=======
}

dependencies {
    // Core Android & Kotlin
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Jetpack Compose
>>>>>>> feature/core-audio-engine
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
<<<<<<< HEAD
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.4")
    
    // Dependency Injection - Hilt
    implementation("com.google.dagger:hilt-android:2.47")
    kapt("com.google.dagger:hilt-android-compiler:2.47")
=======
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // Audio & Media
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media3:media3-ui:1.2.0")
    implementation("androidx.media3:media3-common:1.2.0")
    implementation("androidx.media3:media3-session:1.2.0")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
>>>>>>> feature/core-audio-engine
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
<<<<<<< HEAD
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    
    // Audio and Media
    implementation("androidx.media3:media3-exoplayer:1.1.1")
    implementation("androidx.media3:media3-ui:1.1.1")
    implementation("androidx.media3:media3-common:1.1.1")
    
    // TensorFlow Lite for AI/ML
    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation("org.tensorflow:tensorflow-lite-android:2.13.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.13.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    
    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
=======
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Data Store
    implementation("androidx.datastore:datastore-preferences:1.0.0")
>>>>>>> feature/core-audio-engine
    
    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    
<<<<<<< HEAD
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    
=======
    // Neural Network & AI
    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    
    // Audio Processing Libraries
    implementation("com.github.wendykierp:JTransforms:3.1")
    
    // Networking (for streaming, updates)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Debugging & Development
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("com.facebook.flipper:flipper:0.212.0")
    debugImplementation("com.facebook.soloader:soloader:0.10.5")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.google.truth:truth:1.1.4")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.test:core:1.5.0")
    
    // Android Testing
>>>>>>> feature/core-audio-engine
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
<<<<<<< HEAD
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
=======
    // Performance Testing
    androidTestImplementation("androidx.benchmark:benchmark-junit4:1.2.2")
>>>>>>> feature/core-audio-engine
}