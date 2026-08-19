import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

plugins {
    kotlin("kapt")
    id("questwalk.android.application")
    id("questwalk.android.compose")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.service.gms)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-parcelize")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { stream -> load(stream) }
    }
}

fun signingValue(key: String, envKey: String): String? =
    keystoreProperties.getProperty(key) ?: System.getenv(envKey)

android {
    namespace = "com.hapataka.questwalk"

    defaultConfig {
        applicationId = "com.hapataka.questwalk"
        versionCode = 12
        versionName = "2.1"

        buildConfigField(
            "String",
            "weather_key",
            gradleLocalProperties(rootDir, providers).getProperty("WEATHER_API_KEY")
        )
    }

    signingConfigs {
        create("release") {
            val storePath = signingValue("storeFile", "KEYSTORE_FILE")
            if (storePath != null && file(storePath).exists()) {
                storeFile = file(storePath)
                storePassword = signingValue("storePassword", "KEYSTORE_PASSWORD")
                keyAlias = signingValue("keyAlias", "KEY_ALIAS")
                keyPassword = signingValue("keyPassword", "KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfigs.getByName("release").storeFile?.let {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(projects.feature.main)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.record)
    implementation(projects.feature.quest)
    implementation(projects.feature.weather)
    implementation(projects.feature.home)
    implementation(projects.core.navigation)
    implementation(projects.core.designsystem)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.remote)
    implementation(projects.core.ui)
    implementation(projects.core.service)

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics:22.1.2")
    implementation("com.google.firebase:firebase-auth-ktx:23.1.0")
    implementation("com.google.firebase:firebase-analytics-ktx:22.1.2")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation(libs.play.services.maps)

    implementation(libs.core.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    implementation("androidx.wear.compose:compose-foundation:1.3.1")
    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // OKHttp for 통신 로그
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //Hilt
//    implementation(libs.hilt.android)

    // coil
    implementation(libs.coil)
    implementation(libs.coil.gif)

    // coil-compose
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    //java-string-similarity
    implementation("info.debatty:java-string-similarity:2.0.0")

    //openCV
    implementation(libs.opencv)

    //Proj4j
    implementation("org.locationtech.proj4j:proj4j:1.3.0")

    // Data Store
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-core:1.1.1")

    // kotlinx serialization json
    implementation(libs.kotlinx.serialization.json)

    implementation("androidx.compose.runtime:runtime-livedata:1.7.4")
}
