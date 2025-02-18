import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.library")
    id("questwalk.android.hilt")
}

setNamespace("core.data")

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.remote)
    implementation(projects.core.local)

    implementation(libs.firebase.auth)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
