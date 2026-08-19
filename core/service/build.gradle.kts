import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.library")
    id("questwalk.android.hilt")
}

setNamespace("core.service")

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)

    implementation(libs.core.ktx)
}
