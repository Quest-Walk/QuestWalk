import questwalk.convention.setNamespace

plugins {
    id("questwalk.android.feature")
}

android {
    setNamespace("feature.login")
}

dependencies {
    implementation(libs.coil.compose)
}