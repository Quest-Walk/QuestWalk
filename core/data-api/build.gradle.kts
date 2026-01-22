import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.library")
}

setNamespace("core.dataapi")

dependencies {
    implementation(projects.core.model)
    implementation(libs.coroutines.core)
}
