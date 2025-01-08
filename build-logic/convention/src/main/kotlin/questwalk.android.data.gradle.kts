import questwalk.convention.configurationCoroutineAndroid
import questwalk.convention.configureHiltAndroid

plugins {
    id("questwalk.android.library")
}

configureHiltAndroid()
configurationCoroutineAndroid()

dependencies {
    implementation(project(":core:domain"))
}