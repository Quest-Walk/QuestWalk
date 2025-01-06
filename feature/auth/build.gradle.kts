import questwalk.convention.setNamespace

plugins {
    id("questwalk.android.feature")
}

android {
    setNamespace("feature.auth")
}

dependencies {
    implementation(libs.coil.compose)
}