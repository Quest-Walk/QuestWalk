import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.library")
    id("questwalk.android.hilt")
}

setNamespace("core.remote")

dependencies {
    implementation(projects.core.domain)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
}