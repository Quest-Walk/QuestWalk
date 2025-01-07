import questwalk.convention.configurationCoroutineAndroid
import questwalk.convention.configureHiltAndroid

plugins {
    id("questwalk.android.library")
    id("questwalk.android.compose")
}

configureHiltAndroid()
configurationCoroutineAndroid()

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
}