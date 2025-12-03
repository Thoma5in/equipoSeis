plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")              // Único procesador de anotaciones
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.inventoryapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.inventoryapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        dataBinding = true
        viewBinding = true
    }
}

// KSP args (requerido para Room)
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

// Obligatorio para JUnit 5
tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {

    // ──────────────────────────────────────────────────────────────
    // CORE ANDROID
    // ──────────────────────────────────────────────────────────────
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.7.1")

    // RecyclerView / CardView
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // ──────────────────────────────────────────────────────────────
    // LIFECYCLE
    // ──────────────────────────────────────────────────────────────
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")

    // ──────────────────────────────────────────────────────────────
    // COROUTINAS
    // ──────────────────────────────────────────────────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // ──────────────────────────────────────────────────────────────
    // ROOM (con KSP)
    // ──────────────────────────────────────────────────────────────
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // ──────────────────────────────────────────────────────────────
    // NAVIGATION
    // ──────────────────────────────────────────────────────────────
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // ──────────────────────────────────────────────────────────────
    // FIREBASE
    // ──────────────────────────────────────────────────────────────
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")

    // ──────────────────────────────────────────────────────────────
    // NETWORKING
    // ──────────────────────────────────────────────────────────────
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // ──────────────────────────────────────────────────────────────
    // IMAGE LOADING
    // ──────────────────────────────────────────────────────────────
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ──────────────────────────────────────────────────────────────
    // ANIMACIONES
    // ──────────────────────────────────────────────────────────────
    implementation("com.airbnb.android:lottie:6.7.1")

    // ──────────────────────────────────────────────────────────────
    // BIOMETRICS
    // ──────────────────────────────────────────────────────────────
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // ──────────────────────────────────────────────────────────────
    // HILT / DAGGER (con KSP)
    // ──────────────────────────────────────────────────────────────
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")


    // ──────────────────────────────────────────────────────────────
    // TEST
    // ──────────────────────────────────────────────────────────────

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    //junit4 compatibility
    testImplementation("org.robolectric:robolectric:4.12.1")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.10.0")

    // Mockito (actual)
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")

    // Mockito-Kotlin (actual)
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")

    // Coroutines test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    // LiveData / Architecture testing
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // Android tests
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Jacoco
    debugImplementation("org.jacoco:org.jacoco.core:0.8.14")
}
