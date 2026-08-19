import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.feature")
}

setNamespace("feature.main")

dependencies {
    implementation(projects.core.navigation)
    implementation(projects.feature.splash)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.home)
    implementation(projects.feature.quest)
    implementation(projects.feature.record)
    implementation(projects.feature.weather)
    implementation(projects.feature.myinfo)
    implementation(projects.feature.camera)
    implementation(projects.feature.result)
}
