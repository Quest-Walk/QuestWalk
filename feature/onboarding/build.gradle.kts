import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.feature")
}

android {
    setNamespace("feature.onboarding")

    defaultConfig.buildConfigField(
        "String",
        "GOOGLE_CLIENT_ID",
        gradleLocalProperties(rootDir, providers).getProperty("google_cient_id")
    )

    buildFeatures.buildConfig = true
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.google.android.identity.googleid)
    implementation(libs.kotlinx.serialization.json)
}