import questwalk.convention.setNamespace

plugins {
    id("questwalk.android.library")
}

android {
    setNamespace("core.domain")
}

dependencies {
    api(project(":core:model"))
}
