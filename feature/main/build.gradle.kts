import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.feature")
}

setNamespace("feature.main")

dependencies {
    implementation(projects.core.navigation)
    implementation(projects.feature.onboarding)
}