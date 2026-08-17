import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.feature")
}

setNamespace("feature.result")

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
}
