import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "2.0.0"
}



android {
    namespace = "com.hapataka.questwalk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hapataka.questwalk"
        minSdk = 26
        targetSdk = 34
        versionCode = 12
        versionName = "2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "weather_key",
            gradleLocalProperties(rootDir).getProperty("WEATHER_API_KEY")
        )
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics:22.1.2")
    implementation("com.google.firebase:firebase-auth-ktx:23.1.0")
    implementation("com.google.firebase:firebase-analytics-ktx:22.1.2")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.1")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.1")
    implementation("com.google.mlkit:text-recognition-korean:16.0.1")

    implementation("com.google.android.gms:play-services-mlkit-text-recognition-korean:16.0.1")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")


    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.wear.compose:compose-foundation:1.3.1")
    implementation("androidx.camera:camera-core:1.3.4")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // OKHttp for 통신 로그
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")



    //spinner
    implementation("com.github.skydoves:powerspinner:1.2.7")

    // coil
    implementation(libs.coil)
    implementation(libs.coil.gif)

    // coil-compose
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    //java-string-similarity
    implementation("info.debatty:java-string-similarity:2.0.0")

    //cameraX
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")
    implementation("androidx.camera:camera-extensions:1.3.4")

    //openCV
    implementation("com.quickbirdstudios:opencv:4.5.3")

    // Normal
    implementation("io.github.ParkSangGwon:tedpermission-normal:3.3.0")
    // Coroutine
    implementation("io.github.ParkSangGwon:tedpermission-coroutine:3.3.0")

    //ProgressBar
    implementation("com.github.MackHartley:RoundedProgressBar:3.0.0")

    //Proj4j
    implementation("org.locationtech.proj4j:proj4j:1.3.0")

    // Data Store
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-core:1.1.1")

    // kotlinx serialization json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    implementation("androidx.compose.material3:material3-android:1.3.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.4")
    implementation("androidx.compose.ui:ui:1.7.4")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.7.4")
    implementation("androidx.compose.ui:ui-tooling-preview-desktop:1.7.0")
}
