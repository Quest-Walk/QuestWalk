import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.library")
    id("questwalk.android.hilt")
    alias(libs.plugins.ksp)
}

setNamespace("core.local")

dependencies {
    implementation(projects.core.dataApi)
    implementation(projects.core.domain)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.play.services.location)
    implementation(libs.coroutines.play.services)
}
