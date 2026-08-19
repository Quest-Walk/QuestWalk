import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.feature")
}

android {
    setNamespace("feature.record")
}

dependencies {
    implementation(libs.coil.compose)
}
