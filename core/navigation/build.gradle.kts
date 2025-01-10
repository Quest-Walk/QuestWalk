import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    setNamespace("core.navigation")
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}