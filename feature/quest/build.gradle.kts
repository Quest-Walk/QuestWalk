import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.feature")
}

android {
    setNamespace("feature.quest")
}

dependencies {
    implementation(libs.coil.compose)
}
