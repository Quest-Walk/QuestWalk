import questwalk.convention.configureHiltAndroid
import questwalk.convention.configureKotlinAndroid

plugins {
    id("com.android.application")
}

configureKotlinAndroid()
configureHiltAndroid()