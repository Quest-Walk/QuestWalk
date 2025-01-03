import questwalk.convention.setNamespace

plugins {
    id("questwalk.android.library")
    id("questwalk.android.compose")
}

android {
    setNamespace("core.designsystem")
}