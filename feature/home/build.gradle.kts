import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.feature")
}

android {
    setNamespace("feature.home")
}

dependencies {
    implementation(project(":core:service"))
}
