import questwalk.convention.configureHiltAndroid

plugins {
    id("questwalk.android.library")
    id("questwalk.android.compose")
}

configureHiltAndroid()

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
}