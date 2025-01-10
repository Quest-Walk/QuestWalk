import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.library")
    id("questwalk.android.hilt")
}

setNamespace("core.data")

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.remote)

    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    testImplementation(libs.junit)
}
