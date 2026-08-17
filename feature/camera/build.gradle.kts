import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.feature")
}

android {
    setNamespace("feature.camera")
}

dependencies {
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.androidx.exifinterface)
}
