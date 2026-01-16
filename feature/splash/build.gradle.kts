import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.feature")
}

android {
    setNamespace("feature.splash")
}

dependencies {
    implementation(projects.core.navigation)
}
