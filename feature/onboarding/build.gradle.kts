import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.feature")
}

android {
    setNamespace("feature.onboarding")
}

dependencies {
    implementation(libs.coil.compose)
}