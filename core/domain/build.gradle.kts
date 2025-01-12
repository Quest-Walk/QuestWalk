import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.library")
    kotlin("kapt")
}

android {
    setNamespace("core.domain")
}

dependencies {
    api(projects.core.model)
    api(projects.core.common)
}
